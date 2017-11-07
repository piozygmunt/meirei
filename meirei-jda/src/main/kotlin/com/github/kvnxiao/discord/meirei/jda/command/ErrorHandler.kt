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
import com.github.kvnxiao.discord.meirei.command.CommandContext
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.ChannelType
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import java.util.EnumSet

interface ErrorHandler {

    fun onRateLimit(context: CommandContext, event: MessageReceivedEvent) {
        Meirei.LOGGER.debug("Executing command '${context.properties.id}' ignored due to user ${event.author} being rate-limited")
        if (event.isFromType(ChannelType.PRIVATE)) {
            event.channel.sendMessage("Slow down, you're trying to execute the '${context.alias}' command too fast here.").queue()
        } else {
            event.author.openPrivateChannel().queue {
                it.sendMessage("Slow down there, you're trying to execute the '${context.alias}' command too fast in **${event.guild.name} : ${event.channel.name}**.").queue()
            }
        }
    }

    fun onMissingPermissions(context: CommandContext, event: MessageReceivedEvent, requiredPerms: EnumSet<Permission>) {
        Meirei.LOGGER.debug("${event.author} can't execute command '${context.properties.id}' in ${event.guild.name} : ${event.channel.name} due to missing permissions: $requiredPerms")
        event.author.openPrivateChannel().queue {
            it.sendMessage("Sorry, you do not have permission to execute the **${context.alias}** command in **${event.guild.name} : ${event.channel.name}**.").queue()
        }
    }

    fun onDirectMessageInvalid(context: CommandContext, event: MessageReceivedEvent) {
        Meirei.LOGGER.debug("Execution of command ${context.properties.id} ignored because the command does not allow direct messages.")
    }

}