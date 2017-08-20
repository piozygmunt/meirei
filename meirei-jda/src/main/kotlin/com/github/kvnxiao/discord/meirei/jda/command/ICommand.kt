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
import com.github.kvnxiao.discord.meirei.command.RateLimitManager
import com.github.kvnxiao.discord.meirei.jda.permission.PermissionProperties
import net.dv8tion.jda.core.entities.ChannelType
import net.dv8tion.jda.core.events.message.MessageReceivedEvent

abstract class ICommand(
    var properties: CommandProperties,
    var permissions: PermissionProperties) : ICommandExecutable {

    protected val rateLimitManager = RateLimitManager()

    val subCommandMap: MutableMap<String, ICommand> = mutableMapOf()
    val subCommands: MutableSet<ICommand> = mutableSetOf()

    fun addSubCommand(subCommand: ICommand): ICommand {
        subCommand.properties.aliases.forEach {
            if (subCommandMap.containsKey(it)) {
                Meirei.LOGGER.error("Failed to link sub-command '${subCommand.properties.uniqueName}' to '${this.properties.uniqueName}' as the alias '$it' is already taken!")
                return@forEach
            } else {
                this.subCommandMap.put(it, subCommand)
            }
        }
        subCommands.add(subCommand)
        return this
    }

    fun hasSubCommands(): Boolean = subCommands.isNotEmpty()

    fun deleteSubCommands() {
        if (this.hasSubCommands()) {
            subCommandMap.clear()
            subCommands.forEach(ICommand::deleteSubCommands)
            subCommands.clear()
        }
    }

    fun isNotUserLimited(userId: Long): Boolean {
        return rateLimitManager.isNotUserLimited(userId, this.permissions.props)
    }

    fun isNotRateLimited(guildId: Long, userId: Long): Boolean {
        return rateLimitManager.isNotRateLimited(guildId, userId, this.permissions.props)
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