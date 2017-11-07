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
package com.github.kvnxiao.discord.meirei.jda.command;

import com.github.kvnxiao.discord.meirei.command.CommandContext;
import com.github.kvnxiao.discord.meirei.command.CommandDefaults;
import com.github.kvnxiao.discord.meirei.command.CommandPackage;
import com.github.kvnxiao.discord.meirei.command.CommandProperties;
import com.github.kvnxiao.discord.meirei.jda.permission.LevelDefaults;
import com.github.kvnxiao.discord.meirei.jda.permission.PermissionPropertiesJDA;
import com.github.kvnxiao.discord.meirei.permission.PermissionData;
import com.github.kvnxiao.discord.meirei.permission.PermissionDefaults;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Builder class to help create a (JDA) command package for adding to the command registry
 */
public final class CommandBuilder {

    // Command properties
    private final String id;
    private final Set<String> aliases = new HashSet<>();
    private String prefix = CommandDefaults.PREFIX;
    private String parentId = CommandDefaults.PARENT_ID;
    // Settings
    private boolean execWithSubCommands = CommandDefaults.EXEC_ALONGSIDE_SUBCOMMANDS;
    private boolean isDisabled = CommandDefaults.IS_DISABLED;
    // Metadata
    private String description = CommandDefaults.NO_DESCRIPTION;
    private String usage = CommandDefaults.NO_USAGE;
    // Permission properties
    private boolean allowDm = PermissionDefaults.ALLOW_DIRECT_MSGING;
    private boolean forceDm = PermissionDefaults.FORCE_DIRECT_MSGING;
    private boolean forceDmReply = PermissionDefaults.FORCE_DIRECT_REPLY;
    private boolean removeCallMsg = PermissionDefaults.REMOVE_CALL_MSG;
    private long rateLimitPeriodMs = PermissionDefaults.RATE_LIMIT_PERIOD_MS;
    private long tokensPerPeriod = PermissionDefaults.TOKENS_PER_PERIOD;
    private boolean rateLimitOnGuild = PermissionDefaults.RATE_LIMIT_ON_GUILD;
    private boolean reqBotOwner = PermissionDefaults.REQUIRE_BOT_OWNER;
    private boolean reqGuildOwner = PermissionDefaults.REQUIRE_GUILD_OWNER;
    private boolean reqMention = PermissionDefaults.REQUIRE_MENTION;
    // Command registry settings
    private boolean isRegistryAware = CommandDefaults.IS_REGISTRY_AWARE;
    // Discord permissions
    private EnumSet<Permission> permissionLevel = LevelDefaults.DEFAULT_PERMS_RW;

    /**
     * Creates a command builder instance with the specified unique command id.
     * @param id the unique identifier (name) for the command
     */
    public CommandBuilder(final String id) {
        this.id = id;
    }

    /**
     * Sets the alias(es) for the command.
     * @param aliases the command alias(es)
     * @return the current command builder
     */
    public CommandBuilder aliases(final String... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));
        return this;
    }

    /**
     * Sets the prefix for the command.
     * @param prefix the command prefix
     * @return the current command builder
     */
    public CommandBuilder prefix(final String prefix) {
        this.prefix = prefix;
        return this;
    }

    /**
     * Sets the parentId for this command.
     * @param parentId the parent command's id
     * @return the current command builder
     */
    public CommandBuilder parentId(final String parentId) {
        this.parentId = parentId;
        return this;
    }

    /**
     * Sets whether the command should execute along with its sub-commands.
     * @param execWithSubCommands a boolean
     * @return the current command builder
     */
    public CommandBuilder execWithSubCommands(final boolean execWithSubCommands) {
        this.execWithSubCommands = execWithSubCommands;
        return this;
    }

    /**
     * Sets whether the command is disabled or not.
     * @param isDisabled a boolean
     * @return the current command builder
     */
    public CommandBuilder isDisabled(final boolean isDisabled) {
        this.isDisabled = isDisabled;
        return this;
    }

    /**
     * Sets the description for the command.
     * @param description the description string
     * @return the current command builder
     */
    public CommandBuilder description(final String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the usage details for the command.
     * @param usage the usage string
     * @return the current command builder
     */
    public CommandBuilder usage(final String usage) {
        this.usage = usage;
        return this;
    }

    /**
     * Sets whether the command is allowed to be executed through direct messages to the bot (otherwise it is guild only).
     * @param allowDm a boolean
     * @return the current command builder
     */
    public CommandBuilder allowDirectMessages(final boolean allowDm) {
        this.allowDm = allowDm;
        return this;
    }

    /**
     * Sets whether the command can only be executed through direct messages to the bot from the user.
     * @param forceDm a boolean
     * @return the current command builder
     */
    public CommandBuilder forceDirectMessages(final boolean forceDm) {
        this.forceDm = forceDm;
        return this;
    }

    /**
     * Sets whether the bot is forced to reply with a direct message to the user during command execution.
     * @param forceDmReply a boolean
     * @return the current command builder
     */
    public CommandBuilder forceDirectMessageReply(final boolean forceDmReply) {
        this.forceDmReply = forceDmReply;
        return this;
    }

    /**
     * Sets whether or not the user's message that executed the command should be deleted upon execution.
     * @param removeCallMsg a boolean
     * @return the current command builder
     */
    public CommandBuilder removeCallMessage(final boolean removeCallMsg) {
        this.removeCallMsg = removeCallMsg;
        return this;
    }

    /**
     * Sets the rate limit period in milliseconds for the command call, before the rate limits are reset on a per-period basis.
     * @param rateLimitPeriodMs the period in milliseconds
     * @return the current command builder
     */
    public CommandBuilder rateLimitPeriodMs(final long rateLimitPeriodMs) {
        this.rateLimitPeriodMs = rateLimitPeriodMs;
        return this;
    }

    /**
     * Sets the number of tokens (number of calls to the command) allowed per rate limit period.
     * @param tokensPerPeriod the number of tokens per period
     * @return the current command builder
     */
    public CommandBuilder tokensPerPeriod(final long tokensPerPeriod) {
        this.tokensPerPeriod = tokensPerPeriod;
        return this;
    }

    /**
     * Sets whether the rate limiting for the command should be done on a per-guild basis, or a per-user basis.
     * @param rateLimitOnGuild a boolean
     * @return the current command builder
     */
    public CommandBuilder rateLimitOnGuild(final boolean rateLimitOnGuild) {
        this.rateLimitOnGuild = rateLimitOnGuild;
        return this;
    }

    /**
     * Sets whether the command requires bot owner privileges in order to be successfully executed.
     * @param reqBotOwner a boolean
     * @return the current command builder
     */
    public CommandBuilder requireBotOwner(final boolean reqBotOwner) {
        this.reqBotOwner = reqBotOwner;
        return this;
    }

    /**
     * Sets whether the command requires guild owner privileges in order to be successfully executed.
     * @param reqGuildOwner a boolean
     * @return the current command builder
     */
    public CommandBuilder requireGuildOwner(final boolean reqGuildOwner) {
        this.reqGuildOwner = reqGuildOwner;
        return this;
    }

    /**
     * Sets whether the command requires an '@' mention before the command prefix and alias in order to be executed.
     * @param reqMention a boolean
     * @return the current command builder
     */
    public CommandBuilder requireMention(final boolean reqMention) {
        this.reqMention = reqMention;
        return this;
    }

    /**
     * Sets whether the command is capable of reading the command registry to retrieve information regarding other commands.
     * @param isRegistryAware whether the command is registry aware
     * @return the current command builder
     */
    public CommandBuilder isRegistryAware(final boolean isRegistryAware) {
        this.isRegistryAware = isRegistryAware;
        return this;
    }

    /**
     * Builds the JDA command package using the values set in the builder.
     * @param executable the command method
     * @return the (JDA) command package containing the executable, command properties, and permission properties
     */
    public CommandPackage build(CommandExecutable executable) {
        if (this.aliases.isEmpty()) {
            this.aliases.add(this.id);
        }

        return new CommandPackage(
                new CommandJDA(this.id, this.isRegistryAware) {
                    @Override
                    public void execute(@NotNull CommandContext context, @NotNull MessageReceivedEvent event) {
                        executable.execute(context, event);
                    }
                }, new CommandProperties(this.id, this.aliases, this.prefix, this.description, this.usage, this.execWithSubCommands,
                this.isDisabled, this.parentId),
                new PermissionPropertiesJDA(new PermissionData(this.allowDm, this.forceDm, this.forceDmReply, this.removeCallMsg, this.rateLimitPeriodMs,
                        this.tokensPerPeriod, this.rateLimitOnGuild, this.reqGuildOwner, this.reqBotOwner, this.reqMention),
                        this.permissionLevel)
        );
    }

}
