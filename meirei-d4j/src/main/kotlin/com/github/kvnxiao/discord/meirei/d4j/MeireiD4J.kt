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
package com.github.kvnxiao.discord.meirei.d4j

import com.github.kvnxiao.discord.meirei.Meirei
import com.github.kvnxiao.discord.meirei.command.CommandContext
import com.github.kvnxiao.discord.meirei.d4j.command.CommandD4J
import com.github.kvnxiao.discord.meirei.d4j.command.CommandParserD4J
import com.github.kvnxiao.discord.meirei.d4j.command.DefaultErrorHandler
import com.github.kvnxiao.discord.meirei.d4j.command.ErrorHandler
import com.github.kvnxiao.discord.meirei.d4j.permission.PermissionPropertiesD4J
import com.github.kvnxiao.discord.meirei.utility.splitString
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.events.IListener
import sx.blah.discord.handle.impl.events.ReadyEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IMessage
import java.util.EnumSet

class MeireiD4J(client: IDiscordClient) : Meirei(commandParser = CommandParserD4J()) {

    private var botOwnerId: Long = 0
    private val errorHandler: ErrorHandler = DefaultErrorHandler()
    private val scheduler: Scheduler = Schedulers.newParallel("MeireiExec-pool")

    init {
        // Register message-received event listener
        Flux.create<MessageReceivedEvent> {
            client.dispatcher.registerListener(IListener<MessageReceivedEvent> { event -> it.next(event) })
        }.publishOn(scheduler)
            .doOnNext { Meirei.LOGGER.debug("Received message ${it.message.content} from ${it.author.stringID} ${if (it.channel.isPrivate) "in direct message." else "in guild ${it.guild.stringID}"}") }
            .doOnError { Meirei.LOGGER.error("An error occurred in processing a MessageReceivedEvent! $it") }
            .subscribe(this::consumeMessage)

        // Register ready-event listener
        Mono.create<ReadyEvent> {
            client.dispatcher.registerListener(IListener<ReadyEvent> { event -> it.success(event) })
        }.publishOn(scheduler)
            .doOnSuccess(this::setBotOwner)
            .doOnError { Meirei.LOGGER.error("An error occurred in processing the ReadyEvent! $it") }
            .subscribe()
    }

    private fun setBotOwner(event: ReadyEvent) {
        // Set bot owner ID
        botOwnerId = event.client.applicationOwner.longID
        Meirei.LOGGER.debug("Bot owner ID found: ${java.lang.Long.toUnsignedString(botOwnerId)}")
    }

    private fun consumeMessage(event: MessageReceivedEvent) {
        val message = event.message
        val rawContent = message.content
        val isPrivate = event.channel.isPrivate

        // Split to check for bot mention
        val (firstStr, secondStr) = splitString(rawContent)
        firstStr?.let {
            // Check for bot mention
            val hasBotMention = hasBotMention(it, message)

            // Process for command alias and arguments
            val content = if (hasBotMention) secondStr else rawContent
            content?.let { process(it, event, isPrivate, hasBotMention) }
        }
    }

    private fun process(input: String, event: MessageReceivedEvent, isDirectMsg: Boolean, hasBotMention: Boolean) {
        val (alias, args) = splitString(input)

        alias?.let {
            val command = registry.getCommandByAlias(it) as CommandD4J?
            command?.let {
                val properties = registry.getPropertiesById(it.id)
                val permissions = registry.getPermissionsById(it.id)
                if (properties != null && permissions != null) {
                    // Execute command
                    val context = CommandContext(alias, args, properties, permissions,
                        isDirectMsg, hasBotMention, if (it.registryAware) registry else null)
                    Meirei.LOGGER.debug("Evaluating input: $input")
                    execute(it, context, event)
                }
            }
        }
    }

    private fun execute(command: CommandD4J, context: CommandContext, event: MessageReceivedEvent): Boolean {
        if (!context.properties.isDisabled) {
            // Check sub-commands
            val args = context.args
            if (args != null && registry.hasSubCommands(command.id)) {
                // Try getting a sub-command from the args
                val (subAlias, subArgs) = splitString(args)
                if (subAlias != null) {
                    val subCommand = registry.getSubCommandByAlias(subAlias, command.id) as CommandD4J?
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

    private fun executeCommand(command: CommandD4J, context: CommandContext, event: MessageReceivedEvent): Boolean {
        // Validate mention-only command
        if (!validateMentionOnly(context)) return false
        // Validate permissions
        if (!validatePermissions(context, event)) return false
        // Validate rate-limits
        if (!validateRateLimits(command, context, event)) return false

        command.execute(context, event)
        return true
    }

    private fun hasBotMention(content: String, message: IMessage): Boolean {
        return content == message.client.ourUser.mention(false) || content == message.client.ourUser.mention(true)
    }

    // Validation
    private fun validateRateLimits(command: CommandD4J, context: CommandContext, event: MessageReceivedEvent): Boolean {
        val isNotRateLimited = if (event.channel.isPrivate) {
            command.isNotUserLimited(event.author.longID, context.permissions.data)
        } else {
            command.isNotRateLimited(event.guild.longID, event.author.longID, context.permissions.data)
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
        val permissions = context.permissions as PermissionPropertiesD4J

        if (context.isDirectMessage) {
            if (!permissions.data.allowDmFromSender)
                errorHandler.onDirectMessageInvalid(context, event)
            else
                return permissions.data.allowDmFromSender
        }

        // Check if command requires to be guild owner
        val isGuildOwner = event.guild.owner.longID == event.author.longID
        // Check if command requires to be bot owner
        val isBotOwner = botOwnerId == event.author.longID

        // Check for required user permissions in current channel
        val requiredPerms = EnumSet.copyOf(permissions.level)
        val userPerms = event.channel.getModifiedPermissions(event.author)
        requiredPerms.removeAll(userPerms)

        val hasUserPerms = requiredPerms.isEmpty() && (!permissions.data.reqGuildOwner || isGuildOwner) && (!permissions.data.reqBotOwner || isBotOwner)

        return if (hasUserPerms) {
            true
        } else {
            errorHandler.onMissingPermissions(context, event, requiredPerms)
            false
        }
    }
}
