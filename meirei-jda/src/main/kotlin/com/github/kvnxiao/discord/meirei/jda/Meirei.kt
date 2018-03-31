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
package com.github.kvnxiao.discord.meirei.jda

import com.github.kvnxiao.discord.meirei.command.DiscordCommandPackage
import com.github.kvnxiao.discord.meirei.jda.command.CommandContext
import com.github.kvnxiao.discord.meirei.jda.command.executor.DiscordCommandExecutor
import com.github.kvnxiao.discord.meirei.jda.command.parser.DiscordCommandParser
import com.github.kvnxiao.kommandant.Kommandant
import com.github.kvnxiao.kommandant.command.CommandPackage
import com.github.kvnxiao.kommandant.command.Context
import com.github.kvnxiao.kommandant.command.registry.CommandRegistry
import com.github.kvnxiao.kommandant.command.registry.CommandRegistryImpl
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.ChannelType
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.EventListener
import java.util.concurrent.Executors

class Meirei(private val jdaBuilder: JDABuilder, registry: CommandRegistry) :
    Kommandant(registry, DiscordCommandExecutor(), DiscordCommandParser(), Executors.newScheduledThreadPool(2)) {

    init {
        // Register message-received event listener
        jdaBuilder.addEventListener(EventListener { event ->
            // Received a message event for parsing
            if (event is MessageReceivedEvent) {
                this.consumeMessage(event)
            }
        })
    }

    constructor(jdaBuilder: JDABuilder) : this(jdaBuilder, CommandRegistryImpl())

    override fun addAnnotatedCommands(vararg instances: Any): Boolean {
        val success = super.addAnnotatedCommands(*instances)
        if (success) {
            instances.filter { it is EventListener }.forEach {
                jdaBuilder.addEventListener(it)
            }
        }
        return success
    }

    private fun consumeMessage(event: MessageReceivedEvent) {
        val message = event.message
        val rawContent = message.contentRaw
        val isChannelPrivate = event.isFromType(ChannelType.PRIVATE)
        val botMentionIndex = botMentionNextIndex(rawContent, event.jda.selfUser.id)
        val hasBotMention = (botMentionIndex > 0 && botMentionIndex < rawContent.length)
        val processedMessage = if (hasBotMention) rawContent.substring(botMentionIndex) else rawContent

        processAsync<Any?>(processedMessage, arrayOf(event, isChannelPrivate, hasBotMention))
    }

    @Suppress("UNCHECKED_CAST")
    override fun createContext(alias: String, args: String?, command: CommandPackage<*>, opt: Array<Any>?): Context {
        val discordCommand = command as DiscordCommandPackage<Permission>
        val isDirectMessage = opt!![1] as Boolean
        val hasBotMention = opt[2] as Boolean
        val readOnlyRegistry = if (discordCommand.isRegistryAware) this.registry else null
        return CommandContext(alias, args, discordCommand.properties, discordCommand.permissions,
            discordCommand.permissionLevel, isDirectMessage, hasBotMention, readOnlyRegistry)
    }

    private fun botMentionNextIndex(content: String, id: String): Int {
        if (content[0] == '<' && content[1] == '@') {
            var offset = if (content[2] == '!') 3 else 2
            for (char in id) {
                if (char != content[offset++]) {
                    return 0
                }
            }
            if (content[offset++] == '>') {
                return offset + 1
            }
        }
        return 0
    }
}
