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
package com.github.kvnxiao.discord.meirei.d4j.tests

import com.github.kvnxiao.discord.meirei.command.permission.PermissionProperties
import com.github.kvnxiao.discord.meirei.d4j.Meirei
import com.github.kvnxiao.discord.meirei.d4j.command.CommandContext
import com.github.kvnxiao.discord.meirei.d4j.command.CommandD4J
import com.github.kvnxiao.discord.meirei.d4j.command.DefaultErrorHandler
import com.github.kvnxiao.discord.meirei.d4j.command.DiscordExecutableAction
import com.github.kvnxiao.discord.meirei.d4j.command.permission.PermissionLevelDefaults
import com.github.kvnxiao.discord.meirei.d4j.sendBuffered
import com.github.kvnxiao.discord.meirei.d4j.tests.annotated.AnnotatedCommand
import com.github.kvnxiao.discord.meirei.d4j.tests.annotated.PermissionCommand
import com.github.kvnxiao.discord.meirei.d4j.tests.annotated.ReadyListenerCommand
import com.github.kvnxiao.discord.meirei.d4j.tests.annotated.RegistryAwareCommand
import com.github.kvnxiao.kommandant.command.CommandProperties
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

fun main(args: Array<String>) {
    val token = System.getenv("TEST_BOT_TOKEN")
    requireNotNull(token, { "The environment variable 'TEST_BOT_TOKEN' must be set for logging in." })
    val client = ClientBuilder()
        .withToken(token).build()

    // Add Meirei to discord client
    val meirei = Meirei(client)

    // Add command created through constructors
    val command = CommandD4J(object : DiscordExecutableAction<Unit> {
        override fun execute(context: CommandContext, event: MessageReceivedEvent) {
            event.channel.sendBuffered("This is a command created using constructors.")
        }
    }, CommandProperties("test.constructor", aliases = setOf("constructor", "c"), prefix = "!"),
        PermissionProperties(), DefaultErrorHandler(), PermissionLevelDefaults.DEFAULT_PERMS_RW, false)
    val subCommand = CommandD4J(object : DiscordExecutableAction<Unit> {
        override fun execute(context: CommandContext, event: MessageReceivedEvent) {
            event.channel.sendBuffered("This is a sub-command created using constructors.")
        }
    }, CommandProperties("test.constructor.child", aliases = setOf("sub", "s"), parentId = "test.constructor"),
        PermissionProperties(), DefaultErrorHandler(), PermissionLevelDefaults.DEFAULT_PERMS_RW, false)

    meirei.addCommand(command)
    meirei.addSubCommand(subCommand, command.properties.id)

//    // Builder-based command
//    meirei.addCommands(CommandBuilder("test.builder")
//        .aliases("builder")
//        .build { _, event ->
//            event.channel.sendBuffered("This command was created using a CommandBuilder class.")
//        }
//    )
    // Add annotation-based commands
    meirei.addAnnotatedCommands(
        AnnotatedCommand(),
        PermissionCommand(),
        RegistryAwareCommand(),
        ReadyListenerCommand()
    )

    // Log in to discord
    client.login()
}
