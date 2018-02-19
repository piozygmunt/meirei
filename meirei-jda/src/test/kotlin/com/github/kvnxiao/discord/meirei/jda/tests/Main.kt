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
package com.github.kvnxiao.discord.meirei.jda.tests

import com.github.kvnxiao.discord.meirei.Meirei
import com.github.kvnxiao.discord.meirei.command.CommandContext
import com.github.kvnxiao.discord.meirei.command.CommandPackage
import com.github.kvnxiao.discord.meirei.command.CommandProperties
import com.github.kvnxiao.discord.meirei.jda.MeireiJDA
import com.github.kvnxiao.discord.meirei.jda.command.CommandBuilder
import com.github.kvnxiao.discord.meirei.jda.command.CommandJDA
import com.github.kvnxiao.discord.meirei.jda.command.build
import com.github.kvnxiao.discord.meirei.jda.permission.PermissionPropertiesJDA
import com.github.kvnxiao.discord.meirei.jda.tests.annotated.AnnotatedCommand
import com.github.kvnxiao.discord.meirei.jda.tests.annotated.NestedAnnotatedCommand
import com.github.kvnxiao.discord.meirei.jda.tests.annotated.PermissionCommand
import com.github.kvnxiao.discord.meirei.jda.tests.annotated.ReadyListenerCommand
import com.github.kvnxiao.discord.meirei.jda.tests.annotated.RegistryAwareCommand
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.events.message.MessageReceivedEvent

fun main(args: Array<String>) {
    val token = System.getenv("TEST_BOT_TOKEN")
    requireNotNull(token, { "The environment variable 'TEST_BOT_TOKEN' must be set for logging in." })
    val builder = JDABuilder(AccountType.BOT)
        .setToken(token)

    // Add Meirei to client builder
    val meirei: Meirei = MeireiJDA(builder)

    // Add command created through constructors
    val constructorCommandId = "test.constructor"
    val command = object : CommandJDA(constructorCommandId) {
        override fun execute(context: CommandContext, event: MessageReceivedEvent) {
            event.channel.sendMessage("This is a command created using constructors.").queue()
        }
    }
    val constructorSubCommandId = "test.constructor.child"
    val subCommand = object : CommandJDA(constructorSubCommandId) {
        override fun execute(context: CommandContext, event: MessageReceivedEvent) {
            event.channel.sendMessage("This is a child command created using constructors").queue()
        }
    }

    // Constructor-based command and sub-command
    meirei.addCommands(
        CommandPackage(
            command,
            CommandProperties(constructorCommandId, aliases = setOf("constructor", "cons"), execWithSubCommands = true),
            PermissionPropertiesJDA()
        )
    )
    meirei.addSubCommands(constructorCommandId,
        CommandPackage(
            subCommand,
            CommandProperties(constructorSubCommandId, aliases = setOf("child")),
            PermissionPropertiesJDA()
        )
    )
    // Builder-based command
    meirei.addCommands(CommandBuilder("test.builder")
        .aliases("builder")
        .build { _, event ->
            event.channel.sendMessage("This command was created using a CommandBuilder class.").queue()
        }
    )
    // Add annotation-based commands
    meirei.addAnnotatedCommands(
        AnnotatedCommand(),
        NestedAnnotatedCommand(),
        PermissionCommand(),
        RegistryAwareCommand(),
        ReadyListenerCommand()
    )

    // Build client and log in to discord
    builder.buildAsync()
}
