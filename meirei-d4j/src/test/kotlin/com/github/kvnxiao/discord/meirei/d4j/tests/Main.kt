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
package com.github.kvnxiao.discord.meirei.d4j.tests

import com.github.kvnxiao.discord.meirei.Meirei
import com.github.kvnxiao.discord.meirei.command.CommandContext
import com.github.kvnxiao.discord.meirei.command.CommandPackage
import com.github.kvnxiao.discord.meirei.command.CommandProperties
import com.github.kvnxiao.discord.meirei.d4j.MeireiD4J
import com.github.kvnxiao.discord.meirei.d4j.command.CommandD4J
import com.github.kvnxiao.discord.meirei.d4j.permission.PermissionPropertiesD4J
import com.github.kvnxiao.discord.meirei.d4j.sendBuffered
import com.github.kvnxiao.discord.meirei.d4j.tests.annotated.AnnotatedCommand
import com.github.kvnxiao.discord.meirei.d4j.tests.annotated.NestedAnnotatedCommand
import com.github.kvnxiao.discord.meirei.d4j.tests.annotated.PermissionCommand
import com.github.kvnxiao.discord.meirei.d4j.tests.annotated.RegistryAwareCommand
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

fun main(args: Array<String>) {
    val token = System.getenv("TEST_BOT_TOKEN")
    requireNotNull(token, { "The environment variable 'TEST_BOT_TOKEN' must be set for logging in." })
    val client = ClientBuilder()
        .withToken(token)
        .build()

    // Add Meirei to discord client
    val meirei: Meirei = MeireiD4J(client)

    // Add command created through constructors
    val constructorCommandId = "test.constructor"
    val command = object : CommandD4J(constructorCommandId) {
        override fun execute(context: CommandContext, event: MessageReceivedEvent) {
            event.channel.sendBuffered("This is a command created using constructors.")
        }
    }
    val constructorSubCommandId = "test.constructor.child"
    val subCommand = object : CommandD4J(constructorSubCommandId) {
        override fun execute(context: CommandContext, event: MessageReceivedEvent) {
            event.channel.sendBuffered("This is a child command created through constructors")
        }
    }
    meirei.addCommands(
        CommandPackage(
            command,
            CommandProperties(constructorCommandId, aliases = setOf("constructor", "cons"), execWithSubCommands = true),
            PermissionPropertiesD4J()
        )
    )
    meirei.addSubCommands(constructorCommandId,
        CommandPackage(
            subCommand,
            CommandProperties(constructorSubCommandId, aliases = setOf("child")),
            PermissionPropertiesD4J()
        )
    )
    meirei.addAnnotatedCommands(
        AnnotatedCommand(),
        NestedAnnotatedCommand(),
        PermissionCommand(),
        RegistryAwareCommand()
    )

    // Log in to discord
    client.login()
}