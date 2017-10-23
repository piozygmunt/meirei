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
import com.github.kvnxiao.discord.meirei.command.CommandProperties
import com.github.kvnxiao.discord.meirei.ratelimit.RateLimitManager
import com.github.kvnxiao.discord.meirei.jda.permission.PermissionPropertiesJDA
import com.github.kvnxiao.discord.meirei.ratelimit.DiscordRateLimiter
import com.github.kvnxiao.discord.meirei.utility.GuildId
import com.github.kvnxiao.discord.meirei.utility.UserId
import net.dv8tion.jda.core.entities.ChannelType
import net.dv8tion.jda.core.events.message.MessageReceivedEvent

abstract class ICommand(
    var properties: CommandProperties,
    var permissions: PermissionPropertiesJDA) : CommandExecutable {

    private val rateLimitManager: DiscordRateLimiter = RateLimitManager()

    val subCommandMap: MutableMap<String, ICommand> = mutableMapOf()
    val subCommands: MutableSet<ICommand> = mutableSetOf()

    fun addSubCommand(subCommand: ICommand): ICommand {
        if (!validateAliases(subCommand.properties.aliases)) {
            Meirei.LOGGER.error("Failed to link sub-command '${subCommand.properties.name}' to '${this.properties.name}' due to aliases already existing")
            return this
        }

        subCommand.properties.aliases.forEach {
            this.subCommandMap.put(it, subCommand)
        }
        subCommands.add(subCommand)
        Meirei.LOGGER.debug("Linked sub-command '${subCommand.properties.name}' to parent '${this.properties.name}': prefix '${subCommand.properties.prefix}', aliases '${subCommand.properties.aliases}'")
        return this
    }

    private fun validateAliases(aliases: Set<String>): Boolean {
        return aliases.none { subCommandMap.containsKey(it) }
    }

    fun hasSubCommands(): Boolean = subCommands.isNotEmpty()

    fun deleteSubCommands() {
        if (this.hasSubCommands()) {
            subCommandMap.clear()
            subCommands.forEach(ICommand::deleteSubCommands)
            subCommands.clear()
        }
    }

    fun isNotUserLimited(userId: UserId): Boolean {
        return rateLimitManager.isNotRateLimitedByUser(userId, this.permissions.data)
    }

    fun isNotRateLimited(guildId: GuildId, userId: UserId): Boolean {
        return rateLimitManager.isNotRateLimited(guildId, userId, this.permissions.data)
    }

    override fun toString(): String {
        return this.properties.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ICommand) return false

        if (properties != other.properties) return false
        if (subCommands != other.subCommands) return false

        return true
    }

    override fun hashCode(): Int {
        var result = properties.hashCode()
        result = 31 * result + subCommands.hashCode()
        return result
    }

    fun onRateLimit(context: CommandContext, event: MessageReceivedEvent) {
        if (event.isFromType(ChannelType.PRIVATE)) {
            event.channel.sendMessage("Slow down, you're trying to execute the '${context.alias}' command too fast here.").queue()
        } else {
            event.author.openPrivateChannel().queue {
                it.sendMessage("Slow down there, you're trying to execute the '${context.alias}' command too fast in **${event.guild.name} : ${event.channel.name}**.").queue()
            }
        }
    }

    fun onMissingPerms(context: CommandContext, event: MessageReceivedEvent) {
        event.author.openPrivateChannel().queue {
            it.sendMessage("Sorry, you do not have permission to execute the **${context.alias}** command in **${event.guild.name} : ${event.channel.name}**.").queue()
        }
    }

}