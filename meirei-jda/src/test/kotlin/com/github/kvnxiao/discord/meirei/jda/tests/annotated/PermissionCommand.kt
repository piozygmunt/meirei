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
package com.github.kvnxiao.discord.meirei.jda.tests.annotated

import com.github.kvnxiao.discord.meirei.command.annotations.Permissions
import com.github.kvnxiao.discord.meirei.jda.command.CommandContext
import com.github.kvnxiao.kommandant.command.annotations.Command
import com.github.kvnxiao.kommandant.command.annotations.GroupId
import com.github.kvnxiao.kommandant.command.annotations.Prefix
import net.dv8tion.jda.core.events.message.MessageReceivedEvent

@GroupId("test.annotated.permission")
class PermissionCommand {

    @Command(
        id = "owner",
        aliases = ["owner"]
    )
    @Prefix("!")
    @Permissions(reqBotOwner = true)
    fun commandOwner(context: CommandContext, event: MessageReceivedEvent) {
        event.channel.sendMessage(
            "This is annotated command alpha requiring bot owner privileges. args: ${context.args}").queue()
    }

    @Command(
        id = "guildOwner",
        aliases = ["guild"]
    )
    @Prefix("!")
    @Permissions(reqGuildOwner = true)
    fun commandGuildOwner(context: CommandContext, event: MessageReceivedEvent) {
        event.channel.sendMessage(
            "This is annotated command beta requiring guild owner privileges. args: ${context.args}").queue()
    }

    @Command(
        id = "mention",
        aliases = ["mention"]
    )
    @Permissions(reqMention = true)
    fun commandMention(context: CommandContext, event: MessageReceivedEvent) {
        event.channel.sendMessage(
            "This is an annotated command that requires a mention to activate. args: ${context.args}").queue()
    }
}
