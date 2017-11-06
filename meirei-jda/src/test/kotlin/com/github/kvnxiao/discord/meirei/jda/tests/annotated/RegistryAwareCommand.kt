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
package com.github.kvnxiao.discord.meirei.jda.tests.annotated

import com.github.kvnxiao.discord.meirei.annotations.Command
import com.github.kvnxiao.discord.meirei.annotations.RegistryAware
import com.github.kvnxiao.discord.meirei.command.CommandContext
import net.dv8tion.jda.core.events.message.MessageReceivedEvent

class RegistryAwareCommand {

    companion object {
        const val PREFIX = "/"
    }

    @Command(
        id = "registry",
        aliases = arrayOf("registry"),
        prefix = PREFIX
    )
    @RegistryAware
    fun commandAlpha(context: CommandContext, event: MessageReceivedEvent) {
        event.channel.sendMessage("This is a registry aware command. all command aliases from registry: ${context.readOnlyCommandRegistry?.getAllCommands()?.joinToString { it.command.id }}").queue()
    }

}