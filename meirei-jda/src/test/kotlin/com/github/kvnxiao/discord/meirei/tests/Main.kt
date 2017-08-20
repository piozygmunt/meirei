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

import com.github.kvnxiao.discord.meirei.command.CommandProperties
import com.github.kvnxiao.discord.meirei.jda.command.CommandContext
import com.github.kvnxiao.discord.meirei.jda.command.CommandListener
import com.github.kvnxiao.discord.meirei.jda.command.ICommand
import com.github.kvnxiao.discord.meirei.jda.permission.PermissionProperties
import com.github.kvnxiao.discord.meirei.permission.PermissionData
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.events.message.MessageReceivedEvent

fun main(args: Array<String>) {
    val builder = JDABuilder(AccountType.BOT)
        .setToken("MjcyMTc3MTQyNTUxNTQzODA4.DHf9hg.BvhDTGixfVCaBJK4Y9rC-PUrShg")

    // Add command listener with application owner information
    val commandListener = CommandListener()
    commandListener.registry.addCommand(object : ICommand(
        properties = CommandProperties(
            uniqueName = "ping",
            prefix = "/",
            aliases = setOf("ping", "hello")
        ),
        permissions = PermissionProperties(
            props = PermissionData(
                tokensPerPeriod = 1,
                rateLimitOnGuild = true,
                rateLimitPeriodInMs = 5000,
                allowPrivate = true
            )
        )
    ) {
        override fun executeWith(context: CommandContext, event: MessageReceivedEvent) {
            event.textChannel.sendMessage("pong!").queue()
        }
    }.addSubCommand(object : ICommand(
        properties = CommandProperties(
            uniqueName = "pong",
            aliases = setOf("pong", "pon")
        ),
        permissions = PermissionProperties(
            props = PermissionData()
        )
    ) {
        override fun executeWith(context: CommandContext, event: MessageReceivedEvent) {
            event.textChannel.sendMessage("ok").queue()
        }
    }))
    commandListener.addAnnotatedCommands(AnnotatedCommand::class)
    builder.addEventListener(commandListener)

    val client = builder.buildBlocking()
    commandListener.setOwner(client.asBot().applicationInfo.complete().owner.idLong)
}