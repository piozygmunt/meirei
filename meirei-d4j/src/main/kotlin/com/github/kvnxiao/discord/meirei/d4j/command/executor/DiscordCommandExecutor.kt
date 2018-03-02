/*
 *   Copyright (C) 2017-2018 Ze Hao Xiao
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
package com.github.kvnxiao.discord.meirei.d4j.command.executor

import arrow.core.Either
import com.github.kvnxiao.discord.meirei.command.DiscordCommandPackage
import com.github.kvnxiao.discord.meirei.command.errors.PermissionValidationError
import com.github.kvnxiao.discord.meirei.d4j.command.CommandContext
import com.github.kvnxiao.discord.meirei.d4j.command.CommandErrorHandler
import com.github.kvnxiao.kommandant.command.CommandPackage
import com.github.kvnxiao.kommandant.command.Context
import com.github.kvnxiao.kommandant.command.executor.CommandExecutor
import mu.KotlinLogging
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.Permissions
import sx.blah.discord.util.RateLimitException
import sx.blah.discord.util.RequestBuffer
import java.util.EnumSet

private val LOGGER = KotlinLogging.logger { }

class DiscordCommandExecutor : CommandExecutor {

    companion object {
        val mentionValidationError = PermissionValidationError("Attempt to call mention-only command without a mention was ignored.")
    }

    private var botOwnerId: Long = 0L

    @Suppress("UNCHECKED_CAST")
    override fun <T> execute(command: CommandPackage<*>, context: Context, opt: Array<Any>?): Either<Exception, T> {
        val discordCommand = command as DiscordCommandPackage<Permissions>
        val commandContext = context as CommandContext
        val event = opt!![0] as MessageReceivedEvent
        val errorHandler: CommandErrorHandler = discordCommand.errorHandler as CommandErrorHandler

        // Validate mention-only command
        if (!validateMentionOnly(commandContext)) {
            return Either.left(mentionValidationError)
        }
        // Validate permissions
        if (!validatePermissions(commandContext, event, errorHandler)) {
            return Either.left(
                PermissionValidationError("Permission validation failed for user ${event.author} in attempt to execute command ${command.properties.id}")
            )
        }
        // Validate rate-limits
        if (!validateRateLimits(discordCommand, context, event, errorHandler)) {
            return Either.left(
                PermissionValidationError("Rate-limit validation failed for user ${event.author} in attempt to execute command ${command.properties.id}")
            )
        }

        // Remove call message if set to true
        if (commandContext.permissions.removeCallMsg) {
            RequestBuffer.request {
                try {
                    event.message.delete()
                } catch (e: RateLimitException) {
                    throw e
                }
            }
        }

        return try {
            LOGGER.debug { "Executing command $command with args: \"${commandContext.args}\", requested by user ${event.author} ${if (commandContext.isDirectMessage) "(in a private message)." else "in guild ${event.guild}."}" }
            val response = command.executable.execute(context, opt)
            Either.right(response as T)
        } catch (ex: Exception) {
            LOGGER.error { "Encountered an exception when executing $command with args: \"${commandContext.args}\", requested by user ${event.author} ${if (commandContext.isDirectMessage) "(in a private message)." else "in guild ${event.guild}."}" }
            errorHandler.onError(command, ex)
            Either.left(ex)
        }
    }

    /**
     * Validate for rate-limits
     */
    private fun validateRateLimits(command: DiscordCommandPackage<Permissions>, context: CommandContext, event: MessageReceivedEvent, errorHandler: CommandErrorHandler): Boolean {
        val success = if (context.isDirectMessage) command.isNotUserLimited(event.author.longID, context.permissions)
        else command.isNotRateLimited(event.guild.longID, event.author.longID, context.permissions)
        if (!success) {
            errorHandler.onRateLimit(context, event)
        }
        return success
    }

    /**
     * Validate for mention-only settings
     */
    private fun validateMentionOnly(context: CommandContext): Boolean = context.permissions.requireMention == context.hasBotMention

    /**
     * Validate that the user has the necessary permissions to call the command
     */
    private fun validatePermissions(context: CommandContext, event: MessageReceivedEvent, errorHandler: CommandErrorHandler): Boolean {
        val permissions = context.permissions
        val permissionLevel = context.permissionLevel

        if (context.isDirectMessage) {
            if (!permissions.allowDmFromSender) {
                errorHandler.onDirectMessageInvalid(context, event)
            } else {
                return permissions.allowDmFromSender
            }
        }

        // Check if command requires to be guild owner
        val isGuildOwner = event.guild.owner.longID == event.author.longID
        // Check if command requires to be bot owner
        val isBotOwner = this.getBotOwnerId(event) == event.author.longID

        // Check for required user permissions in current channel
        val requiredPerms = EnumSet.copyOf(permissionLevel)
        val userPerms = event.channel.getModifiedPermissions(event.author)
        requiredPerms.removeAll(userPerms)

        val hasUserPerms = requiredPerms.isEmpty() && (!permissions.reqGuildOwner || isGuildOwner) && (!permissions.reqBotOwner || isBotOwner)

        return if (hasUserPerms) {
            true
        } else {
            errorHandler.onMissingPermissions(context, event, requiredPerms)
            false
        }
    }

    private fun getBotOwnerId(event: MessageReceivedEvent): Long {
        if (botOwnerId == 0L) {
            botOwnerId = event.client.applicationOwner.longID
            return botOwnerId
        }
        return botOwnerId
    }
}
