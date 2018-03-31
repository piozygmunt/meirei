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

import com.github.kvnxiao.discord.meirei.command.annotations.RegistryAware
import com.github.kvnxiao.discord.meirei.d4j.command.CommandContext
import com.github.kvnxiao.discord.meirei.d4j.sendBuffered
import com.github.kvnxiao.kommandant.command.annotations.Command
import com.github.kvnxiao.kommandant.command.annotations.Prefix
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

@Prefix("/")
class RegistryAwareCommand {

    @Command(
        id = "registry",
        aliases = ["registry"]
    )
    @RegistryAware
    fun registryCommand(context: CommandContext, event: MessageReceivedEvent) {
        event.channel.sendBuffered(
            "This is a registry aware command. all command ids from registry: ${context.readOnlyCommandRegistry?.getAllCommands()?.joinToString { it.properties.id }}")
    }
}
