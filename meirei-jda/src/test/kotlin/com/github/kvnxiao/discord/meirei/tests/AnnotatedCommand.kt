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
package com.github.kvnxiao.discord.meirei.tests

import com.github.kvnxiao.discord.meirei.command.Command
import com.github.kvnxiao.discord.meirei.jda.command.CommandContext
import net.dv8tion.jda.core.events.message.MessageReceivedEvent

class AnnotatedCommand {

    @Command(
        prefix = "!",
        uniqueName = "test",
        aliases = arrayOf("test", "te", "ann")
    )
    fun command(context: CommandContext, event: MessageReceivedEvent) {
        event.textChannel.sendMessage("annotated command works!").queue()
    }

    @Command(
        uniqueName = "child",
        aliases = arrayOf("child", "test"),
        parentName = "test"
    )
    fun childCommand(context: CommandContext, event: MessageReceivedEvent) {
        event.textChannel.sendMessage("child command works!").queue()
    }

}