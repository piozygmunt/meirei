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
package com.github.kvnxiao.discord.meirei.jda.command

import com.github.kvnxiao.discord.meirei.Meirei
import com.github.kvnxiao.discord.meirei.utility.SplitString
import com.github.kvnxiao.discord.meirei.utility.ThreadFactory
import net.dv8tion.jda.core.entities.ChannelType
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CommandListener : ListenerAdapter() {

    private val threadPool: ExecutorService = Executors.newFixedThreadPool(System.getProperty("nthreads")?.toIntOrNull() ?: DEFAULT_THREAD_COUNT, ThreadFactory())
    private val executor: CommandExecutor = CommandExecutor()
    val registry: ICommandRegistry = CommandRegistry()

    companion object {
        const val DEFAULT_THREAD_COUNT = 2
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        threadPool.submit {
            val message = event.message
            val rawContent = message.rawContent
            val isPrivate = event.isFromType(ChannelType.PRIVATE)

            // Split to check for bot mention
            val (firstStr, secondStr) = SplitString(rawContent)
            firstStr?.let {
                // Check for bot mention
                val hasBotMention = hasBotMention(it, message)
                val content = if (hasBotMention) secondStr else rawContent
                content?.let {
                    // Process command message
                    Meirei.LOGGER.debug("Processing command: $it")
                    process(content, event, isPrivate, hasBotMention)
                }
            }
        }
    }

    fun process(input: String, event: MessageReceivedEvent, isPrivate: Boolean, hasBotMention: Boolean) {
        val (alias, args) = SplitString(input)

        alias?.let {
            val command: ICommand? = registry.getCommandByAlias(it)
            command?.let {
                executor.execute(it, CommandContext(alias, args, it), event, isPrivate, hasBotMention)
            }
        }
    }

    fun hasBotMention(content: String, message: Message): Boolean {
        val botMention = message.jda.selfUser.asMention
        return content == botMention
    }

    fun setOwner(ownerId: Long) {
        executor.ownerId = ownerId
    }

}