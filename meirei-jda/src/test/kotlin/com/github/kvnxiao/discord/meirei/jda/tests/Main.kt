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

import com.github.kvnxiao.discord.meirei.command.permission.PermissionProperties
import com.github.kvnxiao.discord.meirei.jda.Meirei
import com.github.kvnxiao.discord.meirei.jda.command.CommandContext
import com.github.kvnxiao.discord.meirei.jda.command.CommandJDA
import com.github.kvnxiao.discord.meirei.jda.command.DefaultErrorHandler
import com.github.kvnxiao.discord.meirei.jda.command.DiscordExecutableAction
import com.github.kvnxiao.discord.meirei.jda.command.permission.PermissionLevelDefaults
import com.github.kvnxiao.discord.meirei.jda.tests.annotated.AnnotatedCommand
import com.github.kvnxiao.discord.meirei.jda.tests.annotated.PermissionCommand
import com.github.kvnxiao.discord.meirei.jda.tests.annotated.ReadyListenerCommand
import com.github.kvnxiao.discord.meirei.jda.tests.annotated.RegistryAwareCommand
import com.github.kvnxiao.kommandant.command.CommandProperties
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.events.message.MessageReceivedEvent

fun main(args: Array<String>) {
    val token = System.getenv("TEST_BOT_TOKEN")
    requireNotNull(token, { "The environment variable 'TEST_BOT_TOKEN' must be set for logging in." })
    val builder = JDABuilder(AccountType.BOT)
        .setToken(token)

    // Add Meirei to client builder
    val meirei = Meirei(builder)

    // Add command created through constructors
    val command = CommandJDA(object : DiscordExecutableAction<Unit> {
        override fun execute(context: CommandContext, event: MessageReceivedEvent) {
            event.channel.sendMessage("This is a command created using constructors.").queue()
        }
    }, CommandProperties("test.constructor", aliases = setOf("constructor", "c"), prefix = "!"),
        PermissionProperties(), DefaultErrorHandler(), PermissionLevelDefaults.DEFAULT_PERMS_RW,
        false)
    val subCommand = CommandJDA(object : DiscordExecutableAction<Unit> {
        override fun execute(context: CommandContext, event: MessageReceivedEvent) {
            event.channel.sendMessage("This is a sub-command created using constructors.").queue()
        }
    }, CommandProperties("test.constructor.child", aliases = setOf("sub", "s"), parentId = "test.constructor"),
        PermissionProperties(), DefaultErrorHandler(), PermissionLevelDefaults.DEFAULT_PERMS_RW,
        false)

    meirei.addCommand(command)
    meirei.addSubCommand(subCommand, command.properties.id)
//    // Builder-based command
//    meirei.addCommands(CommandBuilder("test.builder")
//        .aliases("builder")
//        .build { _, event ->
//            event.channel.sendMessage("This command was created using a CommandBuilder class.").queue()
//        }
//    )
    // Add annotation-based commands
    meirei.addAnnotatedCommands(
        AnnotatedCommand(),
        PermissionCommand(),
        RegistryAwareCommand(),
        ReadyListenerCommand()
    )

    // Build client and log in to discord
    builder.buildAsync()
}
