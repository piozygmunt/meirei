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
package com.github.kvnxiao.discord.meirei.jda.command

import com.github.kvnxiao.discord.meirei.Meirei
import com.github.kvnxiao.discord.meirei.utility.SplitString
import net.dv8tion.jda.core.entities.ChannelType
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import java.lang.reflect.InvocationTargetException
import java.util.EnumSet

class CommandExecutor {

    var ownerId: Long = 0

    fun execute(command: ICommand, context: CommandContext, event: MessageReceivedEvent, isPrivate: Boolean, hasBotMention: Boolean): Boolean {
        // Execute command if it's not disabled
        if (!command.properties.isDisabled) {
            try {
                if (context.args != null && command.hasSubCommands()) {
                    val (subAlias, subArgs) = SplitString(context.args)
                    if (subAlias != null) {
                        val subCommand: ICommand? = command.subCommandMap[subAlias]
                        subCommand?.let {
                            return executeWithSubCommand(command, context, it, subAlias, subArgs, event, isPrivate, hasBotMention)
                        }
                    }
                }
                return executeCommand(command, context, event, isPrivate, hasBotMention)
            } catch (e: InvocationTargetException) {
                Meirei.LOGGER.error("${e.localizedMessage}: Failed to invoke method bound to command '$command'!")
            } catch (e: IllegalAccessException) {
                Meirei.LOGGER.error("${e.localizedMessage}: Failed to access method definition for command '$command'!")
            }
        }
        return false
    }

    private fun executeWithSubCommand(mainCommand: ICommand, mainContext: CommandContext, subCommand: ICommand, alias: String, args: String?, event: MessageReceivedEvent, isPrivate: Boolean, hasBotMention: Boolean): Boolean {
        val subContext = CommandContext(alias, args, subCommand)

        // Execute the parent command alongside sub-command if this property is enabled
        if (mainCommand.properties.execWithSubCommands) {
            executeCommand(mainCommand, mainContext, event, isPrivate, hasBotMention)
        }

        // Execute the sub-command
        return execute(subCommand, subContext, event, isPrivate, hasBotMention)
    }

    private fun executeCommand(command: ICommand, context: CommandContext, event: MessageReceivedEvent, isPrivate: Boolean, hasBotMention: Boolean): Boolean {
        // Validate mention-only command
        if (!validateMentionOnly(command, hasBotMention)) return false
        // Validate permissions
        if (!validatePermissions(command, context, event, isPrivate)) return false
        // Validate rate-limits
        if (!validateRateLimits(command, context, event)) return false

        Meirei.LOGGER.debug("Executing command '${command.properties.uniqueName}' for user ${event.author}")
        command.executeWith(context, event)
        return true
    }

    private fun validateRateLimits(command: ICommand, context: CommandContext, event: MessageReceivedEvent): Boolean {
        val isNotRateLimited = if (event.isFromType(ChannelType.PRIVATE)) {
            command.isNotUserLimited(event.author.idLong)
        } else {
            command.isNotRateLimited(event.guild.idLong, event.author.idLong)
        }
        if (!isNotRateLimited) {
            Meirei.LOGGER.debug("Executing command '$command' ignored due to user ${event.author} being rate-limited")
            command.onRateLimit(context, event)
            return false
        }
        return true
    }

    private fun validateMentionOnly(command: ICommand, hasBotMention: Boolean): Boolean {
        if (command.permissions.props.requireMention == hasBotMention) {
            return true
        }
        Meirei.LOGGER.debug("Executing command '$command' failed due to it requiring a bot mention at the start of the message.")
        return false
    }

    private fun validatePermissions(command: ICommand, context: CommandContext, event: MessageReceivedEvent, isPrivate: Boolean): Boolean {
        val perms = command.permissions

        if (isPrivate) {
            if (!perms.props.allowPrivate)
                Meirei.LOGGER.debug("Execution of command $command ignored because the command does not allow private messages")
            else
                return perms.props.allowPrivate
        }

        // Check if command requires to be guild owner
        val isGuildOwner = event.guild.owner.user.idLong == event.author.idLong
        // Check if command requires to be bot owner
        val isBotOwner = ownerId == event.author.idLong

        // Check for required user permissions in current channel
        val requiredPerms = EnumSet.copyOf(perms.level)
        val userPerms = event.member.getPermissions(event.textChannel)
        requiredPerms.removeAll(userPerms)

        var hasUserPerms = requiredPerms.isEmpty()

        // Check if guild owner
        hasUserPerms = hasUserPerms && (!command.permissions.props.reqGuildOwner || isGuildOwner)

        // Check if bot owner
        hasUserPerms = hasUserPerms && (!command.permissions.props.reqBotOwner || isBotOwner)

        return if (hasUserPerms) {
            true
        } else {
            Meirei.LOGGER.debug("${event.author} can't execute command '${context.properties.uniqueName}' in ${event.guild.name} : ${event.channel.name} due to missing permissions: $requiredPerms")
            command.onMissingPerms(context, event)
            false
        }
    }

}