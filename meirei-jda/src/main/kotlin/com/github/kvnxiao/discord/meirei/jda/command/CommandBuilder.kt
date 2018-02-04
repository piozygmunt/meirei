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
package com.github.kvnxiao.discord.meirei.jda.command

import com.github.kvnxiao.discord.meirei.command.CommandContext
import com.github.kvnxiao.discord.meirei.command.CommandDefaults
import com.github.kvnxiao.discord.meirei.command.CommandPackage
import com.github.kvnxiao.discord.meirei.command.CommandProperties
import com.github.kvnxiao.discord.meirei.jda.permission.LevelDefaults
import com.github.kvnxiao.discord.meirei.jda.permission.PermissionPropertiesJDA
import com.github.kvnxiao.discord.meirei.permission.PermissionData
import com.github.kvnxiao.discord.meirei.permission.PermissionDefaults
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import java.util.Arrays
import java.util.HashSet

/**
 * Builder class to help create a (JDA) command package for adding to the command registry
 */
class CommandBuilder
/**
 * Creates a command builder instance with the specified unique command id.
 * @param id the unique identifier (name) for the command
 */
(private val id: String) {

    // Command properties
    private val aliases = HashSet<String>()
    private var prefix = CommandDefaults.PREFIX
    private var parentId = CommandDefaults.PARENT_ID
    // Settings
    private var execWithSubCommands = CommandDefaults.EXEC_ALONGSIDE_SUBCOMMANDS
    private var isDisabled = CommandDefaults.IS_DISABLED
    // Metadata
    private var description = CommandDefaults.NO_DESCRIPTION
    private var usage = CommandDefaults.NO_USAGE
    // Permission properties
    private var allowDm = PermissionDefaults.ALLOW_DIRECT_MSGING
    private var forceDm = PermissionDefaults.FORCE_DIRECT_MSGING
    private var forceDmReply = PermissionDefaults.FORCE_DIRECT_REPLY
    private var removeCallMsg = PermissionDefaults.REMOVE_CALL_MSG
    private var rateLimitPeriodMs = PermissionDefaults.RATE_LIMIT_PERIOD_MS
    private var tokensPerPeriod = PermissionDefaults.TOKENS_PER_PERIOD
    private var rateLimitOnGuild = PermissionDefaults.RATE_LIMIT_ON_GUILD
    private var reqBotOwner = PermissionDefaults.REQUIRE_BOT_OWNER
    private var reqGuildOwner = PermissionDefaults.REQUIRE_GUILD_OWNER
    private var reqMention = PermissionDefaults.REQUIRE_MENTION
    // Command registry settings
    private var isRegistryAware = CommandDefaults.IS_REGISTRY_AWARE
    // Discord permissions
    private var permissionLevel = LevelDefaults.DEFAULT_PERMS_RW

    /**
     * Sets the alias(es) for the command.
     * @param aliases the command alias(es)
     * @return the current command builder
     */
    fun aliases(vararg aliases: String): CommandBuilder {
        this.aliases.addAll(Arrays.asList(*aliases))
        return this
    }

    /**
     * Sets the prefix for the command.
     * @param prefix the command prefix
     * @return the current command builder
     */
    fun prefix(prefix: String): CommandBuilder {
        this.prefix = prefix
        return this
    }

    /**
     * Sets the parentId for this command.
     * @param parentId the parent command's id
     * @return the current command builder
     */
    fun parentId(parentId: String): CommandBuilder {
        this.parentId = parentId
        return this
    }

    /**
     * Sets whether the command should execute along with its sub-commands.
     * @param execWithSubCommands a boolean
     * @return the current command builder
     */
    fun execWithSubCommands(execWithSubCommands: Boolean): CommandBuilder {
        this.execWithSubCommands = execWithSubCommands
        return this
    }

    /**
     * Sets whether the command is disabled or not.
     * @param isDisabled a boolean
     * @return the current command builder
     */
    fun isDisabled(isDisabled: Boolean): CommandBuilder {
        this.isDisabled = isDisabled
        return this
    }

    /**
     * Sets the description for the command.
     * @param description the description string
     * @return the current command builder
     */
    fun description(description: String): CommandBuilder {
        this.description = description
        return this
    }

    /**
     * Sets the usage details for the command.
     * @param usage the usage string
     * @return the current command builder
     */
    fun usage(usage: String): CommandBuilder {
        this.usage = usage
        return this
    }

    /**
     * Sets whether the command is allowed to be executed through direct messages to the bot (otherwise it is guild only).
     * @param allowDm a boolean
     * @return the current command builder
     */
    fun allowDirectMessages(allowDm: Boolean): CommandBuilder {
        this.allowDm = allowDm
        return this
    }

    /**
     * Sets whether the command can only be executed through direct messages to the bot from the user.
     * @param forceDm a boolean
     * @return the current command builder
     */
    fun forceDirectMessages(forceDm: Boolean): CommandBuilder {
        this.forceDm = forceDm
        return this
    }

    /**
     * Sets whether the bot is forced to reply with a direct message to the user during command execution.
     * @param forceDmReply a boolean
     * @return the current command builder
     */
    fun forceDirectMessageReply(forceDmReply: Boolean): CommandBuilder {
        this.forceDmReply = forceDmReply
        return this
    }

    /**
     * Sets whether or not the user's message that executed the command should be deleted upon execution.
     * @param removeCallMsg a boolean
     * @return the current command builder
     */
    fun removeCallMessage(removeCallMsg: Boolean): CommandBuilder {
        this.removeCallMsg = removeCallMsg
        return this
    }

    /**
     * Sets the rate limit period in milliseconds for the command call, before the rate limits are reset on a per-period basis.
     * @param rateLimitPeriodMs the period in milliseconds
     * @return the current command builder
     */
    fun rateLimitPeriodMs(rateLimitPeriodMs: Long): CommandBuilder {
        this.rateLimitPeriodMs = rateLimitPeriodMs
        return this
    }

    /**
     * Sets the number of tokens (number of calls to the command) allowed per rate limit period.
     * @param tokensPerPeriod the number of tokens per period
     * @return the current command builder
     */
    fun tokensPerPeriod(tokensPerPeriod: Long): CommandBuilder {
        this.tokensPerPeriod = tokensPerPeriod
        return this
    }

    /**
     * Sets whether the rate limiting for the command should be done on a per-guild basis, or a per-user basis.
     * @param rateLimitOnGuild a boolean
     * @return the current command builder
     */
    fun rateLimitOnGuild(rateLimitOnGuild: Boolean): CommandBuilder {
        this.rateLimitOnGuild = rateLimitOnGuild
        return this
    }

    /**
     * Sets whether the command requires bot owner privileges in order to be successfully executed.
     * @param reqBotOwner a boolean
     * @return the current command builder
     */
    fun requireBotOwner(reqBotOwner: Boolean): CommandBuilder {
        this.reqBotOwner = reqBotOwner
        return this
    }

    /**
     * Sets whether the command requires guild owner privileges in order to be successfully executed.
     * @param reqGuildOwner a boolean
     * @return the current command builder
     */
    fun requireGuildOwner(reqGuildOwner: Boolean): CommandBuilder {
        this.reqGuildOwner = reqGuildOwner
        return this
    }

    /**
     * Sets whether the command requires an '@' mention before the command prefix and alias in order to be executed.
     * @param reqMention a boolean
     * @return the current command builder
     */
    fun requireMention(reqMention: Boolean): CommandBuilder {
        this.reqMention = reqMention
        return this
    }

    /**
     * Sets the command's discord permissions that are required for a user to execute the command
     * @param permissions the required discord permissions
     * @return the current command builder
     */
    fun permissionLevel(vararg permissions: Permission): CommandBuilder {
        this.permissionLevel.clear()
        this.permissionLevel.addAll(permissions)
        return this
    }

    /**
     * Sets whether the command is capable of reading the command registry to retrieve information regarding other commands.
     * @param isRegistryAware whether the command is registry aware
     * @return the current command builder
     */
    fun isRegistryAware(isRegistryAware: Boolean): CommandBuilder {
        this.isRegistryAware = isRegistryAware
        return this
    }

    /**
     * Builds the JDA command package using the values set in the builder.
     * @param executable the command method
     * @return the (JDA) command package containing the executable, command properties, and permission properties
     */
    fun build(executable: CommandExecutable): CommandPackage {
        if (this.aliases.isEmpty()) {
            this.aliases.add(this.id)
        }

        return CommandPackage(
            object : CommandJDA(this.id, this.isRegistryAware) {
                override fun execute(context: CommandContext, event: MessageReceivedEvent) {
                    executable.execute(context, event)
                }
            }, CommandProperties(this.id, this.aliases, this.prefix, this.description, this.usage, this.execWithSubCommands,
            this.isDisabled, this.parentId),
            PermissionPropertiesJDA(PermissionData(this.allowDm, this.forceDm, this.forceDmReply, this.removeCallMsg, this.rateLimitPeriodMs,
                this.tokensPerPeriod, this.rateLimitOnGuild, this.reqGuildOwner, this.reqBotOwner, this.reqMention),
                this.permissionLevel)
        )
    }
}

/**
 * Kotlin-based lambda helper for building CommandPackages
 */
fun CommandBuilder.build(execute: (context: CommandContext, event: MessageReceivedEvent) -> Unit): CommandPackage {
    return this.build(object : CommandExecutable {
        override fun execute(context: CommandContext, event: MessageReceivedEvent) {
            execute.invoke(context, event)
        }
    })
}
