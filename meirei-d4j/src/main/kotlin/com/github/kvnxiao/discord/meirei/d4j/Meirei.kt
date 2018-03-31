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
package com.github.kvnxiao.discord.meirei.d4j

import com.github.kvnxiao.discord.meirei.command.DiscordCommandPackage
import com.github.kvnxiao.discord.meirei.d4j.command.CommandContext
import com.github.kvnxiao.discord.meirei.d4j.command.executor.DiscordCommandExecutor
import com.github.kvnxiao.discord.meirei.d4j.command.parser.DiscordCommandParser
import com.github.kvnxiao.kommandant.Kommandant
import com.github.kvnxiao.kommandant.command.CommandPackage
import com.github.kvnxiao.kommandant.command.Context
import com.github.kvnxiao.kommandant.command.registry.CommandRegistry
import com.github.kvnxiao.kommandant.command.registry.CommandRegistryImpl
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.api.events.IListener
import sx.blah.discord.api.internal.json.objects.EmbedObject
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.Permissions
import sx.blah.discord.util.RateLimitException
import sx.blah.discord.util.RequestBuffer
import java.util.concurrent.Executors

class Meirei(private val client: IDiscordClient, registry: CommandRegistry) :
    Kommandant(registry, DiscordCommandExecutor(), DiscordCommandParser(), Executors.newScheduledThreadPool(2)) {

    init {
        // Register message-received event listener
        client.dispatcher.registerListener(IListener { event: MessageReceivedEvent ->
            // Received a message event for parsing
            this.consumeMessage(event)
        })
    }

    constructor(client: IDiscordClient) : this(client, CommandRegistryImpl())

    override fun addAnnotatedCommands(vararg instances: Any): Boolean {
        val success = super.addAnnotatedCommands(*instances)
        if (success) {
            instances.filter {
                it is IListener<*> ||
                it::class.java.methods.any { it.isAnnotationPresent(EventSubscriber::class.java) }
            }.forEach {
                client.dispatcher.registerListener(it)
            }
        }
        return success
    }

    private fun consumeMessage(event: MessageReceivedEvent) {
        val message = event.message
        val rawContent = message.content
        val isChannelPrivate = event.channel.isPrivate
        val botMentionIndex = botMentionNextIndex(rawContent, event.client.ourUser.stringID)
        val hasBotMention = (botMentionIndex > 0 && botMentionIndex < rawContent.length)
        val processedMessage = if (hasBotMention) rawContent.substring(botMentionIndex) else rawContent

        processAsync<Any?>(processedMessage, arrayOf(event, isChannelPrivate, hasBotMention))
    }

    @Suppress("UNCHECKED_CAST")
    override fun createContext(alias: String, args: String?, command: CommandPackage<*>, opt: Array<Any>?): Context {
        val discordCommand = command as DiscordCommandPackage<Permissions>
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

fun IChannel.sendBuffered(content: String): RequestBuffer.RequestFuture<Void> = RequestBuffer.request {
    try {
        this.sendMessage(content)
    } catch (e: RateLimitException) {
        throw e
    }
}

fun IChannel.sendBuffered(embed: EmbedObject): RequestBuffer.RequestFuture<Void> = RequestBuffer.request {
    try {
        this.sendMessage(embed)
    } catch (e: RateLimitException) {
        throw e
    }
}
