/*
 *   Copyright (C) 2017 Ze Hao Xiao
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.github.kvnxiao.discord.meirei.jda

import com.github.kvnxiao.discord.meirei.Meirei
import com.github.kvnxiao.discord.meirei.annotations.parser.AnnotationParser
import com.github.kvnxiao.discord.meirei.command.CommandContext
import com.github.kvnxiao.discord.meirei.jda.command.CommandJDA
import com.github.kvnxiao.discord.meirei.jda.command.CommandParser
import com.github.kvnxiao.discord.meirei.jda.command.ErrorHandler
import com.github.kvnxiao.discord.meirei.jda.permission.PermissionPropertiesJDA
import com.github.kvnxiao.discord.meirei.utility.NamedThreadFactory
import com.github.kvnxiao.discord.meirei.utility.splitString
import net.dv8tion.jda.core.entities.ChannelType
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.events.ReadyEvent
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.EventListener
import java.util.EnumSet
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MeireiJDA : Meirei(), EventListener {

    private var botOwnerId: Long = 0
    private val errorHandler = ErrorHandler()
    private val commandParser: AnnotationParser = CommandParser()

    companion object {
        const val DEFAULT_THREAD_COUNT = 2
    }

    private val threadPool: ExecutorService = Executors.newFixedThreadPool(System.getProperty(Meirei.DEFAULT_THREAD_ENV_NAME)?.toIntOrNull() ?: DEFAULT_THREAD_COUNT, NamedThreadFactory("MeireiExec"))

    override fun onEvent(event: Event) {
        when (event) {
            is MessageReceivedEvent -> onMessageReceived(event)
            is ReadyEvent -> initialize(event)
        }
    }

    private fun initialize(event: ReadyEvent) {
        // Set bot owner ID
        event.jda.asBot().applicationInfo.queue {
            botOwnerId = it.owner.idLong
        }
    }

    private fun onMessageReceived(event: MessageReceivedEvent) {
        threadPool.submit {
            val message = event.message
            val rawContent = message.rawContent
            val isPrivate = event.isFromType(ChannelType.PRIVATE)

            // Split to check for bot mention
            val (firstStr, secondStr) = splitString(rawContent)
            firstStr?.let {
                // Check for bot mention
                val hasBotMention = hasBotMention(it, message)
                val content = if (hasBotMention) secondStr else rawContent
                content?.let {
                    process(content, event, isPrivate, hasBotMention)
                }
            }
        }
    }

    private fun process(input: String, event: MessageReceivedEvent, isDirectMsg: Boolean, hasBotMention: Boolean) {
        val (alias, args) = splitString(input)

        alias?.let {
            val command = registry.getCommandByAlias(it) as CommandJDA?
            command?.let {
                val properties = registry.getPropertiesById(it.id)
                val permissions = registry.getPermissionsById(it.id)
                if (properties != null && permissions != null) {
                    // Execute command
                    val context = CommandContext(alias, args, properties, permissions,
                        isDirectMsg, hasBotMention, if (it.registryAware) registry else null)
                    Meirei.LOGGER.debug("Processing command: ${it.id}")
                    execute(it, context, event)
                }
            }
        }
    }

    private fun execute(command: CommandJDA, context: CommandContext, event: MessageReceivedEvent): Boolean {
        if (!context.properties.isDisabled) {
            // Check sub-commands
            val args = context.args
            if (args != null && registry.hasSubCommands(command.id)) {
                // Try getting a sub-command from the args
                val (subAlias, subArgs) = splitString(args)
                if (subAlias != null) {
                    val subCommand = registry.getSubCommandByAlias(subAlias, command.id) as CommandJDA?
                    if (subCommand != null) {
                        val subProperties = registry.getPropertiesById(subCommand.id)
                        val subPermissions = registry.getPermissionsById(subCommand.id)
                        if (subProperties != null && subPermissions != null) {
                            // Execute sub-command
                            val subContext = CommandContext(subAlias, subArgs, subProperties, subPermissions,
                                context.isDirectMessage, context.hasBotMention, if (subCommand.registryAware) registry else null)
                            // Execute parent-command if the boolean value is true
                            if (context.properties.execWithSubCommands) command.execute(context, event)
                            return execute(subCommand, subContext, event)
                        }
                    }
                }
            }
            return executeCommand(command, context, event)
        }
        return false
    }

    private fun executeCommand(command: CommandJDA, context: CommandContext, event: MessageReceivedEvent): Boolean {
        // Validate mention-only command
        if (!validateMentionOnly(context)) return false
        // Validate permissions
        if (!validatePermissions(context, event)) return false
        // Validate rate-limits
        if (!validateRateLimits(command, context, event)) return false

        command.execute(context, event)
        return true
    }

    private fun hasBotMention(content: String, message: Message): Boolean {
        val botMention = message.jda.selfUser.asMention
        return content == botMention
    }

    // Validation
    private fun validateRateLimits(command: CommandJDA, context: CommandContext, event: MessageReceivedEvent): Boolean {
        val isNotRateLimited = if (event.isFromType(ChannelType.PRIVATE)) {
            command.isNotUserLimited(event.author.idLong, context.permissions.data)
        } else {
            command.isNotRateLimited(event.guild.idLong, event.author.idLong, context.permissions.data)
        }
        if (!isNotRateLimited) {
            errorHandler.onRateLimit(context, event)
            return false
        }
        return true
    }

    private fun validateMentionOnly(context: CommandContext): Boolean {
        return context.permissions.data.requireMention == context.hasBotMention
    }

    private fun validatePermissions(context: CommandContext, event: MessageReceivedEvent): Boolean {
        val permissions = context.permissions as PermissionPropertiesJDA

        if (context.isDirectMessage) {
            if (!permissions.data.allowDmFromSender)
                errorHandler.onDirectMessageInvalid(context, event)
            else
                return permissions.data.allowDmFromSender
        }

        // Check if command requires to be guild owner
        val isGuildOwner = event.guild.owner.user.idLong == event.author.idLong
        // Check if command requires to be bot owner
        val isBotOwner = botOwnerId == event.author.idLong

        // Check for required user permissions in current channel
        val requiredPerms = EnumSet.copyOf(permissions.level)
        val userPerms = event.member.getPermissions(event.textChannel)
        requiredPerms.removeAll(userPerms)

        var hasUserPerms = requiredPerms.isEmpty()

        // Check if guild owner
        hasUserPerms = hasUserPerms && (!permissions.data.reqGuildOwner || isGuildOwner)

        // Check if bot owner
        hasUserPerms = hasUserPerms && (!permissions.data.reqBotOwner || isBotOwner)

        return if (hasUserPerms) {
            true
        } else {
            errorHandler.onMissingPermissions(context, event, requiredPerms)
            false
        }
    }

}