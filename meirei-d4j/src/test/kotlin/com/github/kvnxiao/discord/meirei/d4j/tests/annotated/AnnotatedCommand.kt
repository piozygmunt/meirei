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
package com.github.kvnxiao.discord.meirei.d4j.tests.annotated

import com.github.kvnxiao.discord.meirei.d4j.command.CommandContext
import com.github.kvnxiao.discord.meirei.d4j.sendBuffered
import com.github.kvnxiao.kommandant.command.annotations.Command
import com.github.kvnxiao.kommandant.command.annotations.GroupId
import com.github.kvnxiao.kommandant.command.annotations.Prefix
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

@GroupId("test.annotated.normal")
@Prefix("@")
class AnnotatedCommand {

    @Command(
        id = "annotated",
        aliases = ["annotated", "ann"]
    )
    fun mainCommand(context: CommandContext, event: MessageReceivedEvent) {
        event.channel.sendBuffered("This is an annotated command. args: ${context.args}")
    }

    @Command(
        id = "alpha",
        aliases = ["alpha", "a"],
        parentId = "annotated"
    )
    fun commandAlpha(context: CommandContext, event: MessageReceivedEvent) {
        event.channel.sendBuffered("A. args: ${context.args}")
    }

    @Command(
        id = "beta",
        aliases = ["beta", "b"],
        parentId = "alpha"
    )
    fun commandBeta(context: CommandContext, event: MessageReceivedEvent) {
        event.channel.sendBuffered("B. args: ${context.args}")
    }

    @Command(
        id = "charlie",
        aliases = ["charlie", "c"],
        parentId = "beta"
    )
    fun commandCharlie(context: CommandContext, event: MessageReceivedEvent) {
        event.channel.sendBuffered("C. args: ${context.args}")
    }

    @Command(
        id = "exception",
        aliases = ["exception", "ex"]
    )
    fun exception(context: CommandContext, event: MessageReceivedEvent) {
        throw RuntimeException("this is a runtime exception")
    }
}
