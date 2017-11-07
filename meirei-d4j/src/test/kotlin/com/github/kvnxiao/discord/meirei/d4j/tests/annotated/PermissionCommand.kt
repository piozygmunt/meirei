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
package com.github.kvnxiao.discord.meirei.d4j.tests.annotated

import com.github.kvnxiao.discord.meirei.annotations.Command
import com.github.kvnxiao.discord.meirei.annotations.CommandGroup
import com.github.kvnxiao.discord.meirei.annotations.Permissions
import com.github.kvnxiao.discord.meirei.command.CommandContext
import com.github.kvnxiao.discord.meirei.d4j.sendBuffered
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

@CommandGroup("test.annotated.permission")
class PermissionCommand {

    companion object {
        const val PREFIX = "!"
    }

    @Command(
        id = "alpha",
        aliases = arrayOf("alpha"),
        prefix = PREFIX
    )
    @Permissions(reqBotOwner = true)
    fun commandAlpha(context: CommandContext, event: MessageReceivedEvent) {
        event.channel.sendBuffered("This is annotated command alpha requiring bot owner privileges. args: ${context.args}")
    }

    @Command(
        id = "beta",
        aliases = arrayOf("beta"),
        prefix = PREFIX
    )
    @Permissions(reqGuildOwner = true)
    fun commandBeta(context: CommandContext, event: MessageReceivedEvent) {
        event.channel.sendBuffered("This is annotated command beta requiring guild owner privileges. args: ${context.args}")
    }

    @Command(
        id = "mention",
        aliases = arrayOf("mention"),
        prefix = PREFIX
    )
    @Permissions(reqMention = true)
    fun commandMention(context: CommandContext, event: MessageReceivedEvent) {
        event.channel.sendBuffered("This is an annotated command that requires a mention to activate. args: ${context.args}")
    }
}
