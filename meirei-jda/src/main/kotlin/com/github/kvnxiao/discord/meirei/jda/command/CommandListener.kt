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
import com.github.kvnxiao.discord.meirei.jda.external.ExternalCommandLoader
import com.github.kvnxiao.discord.meirei.utility.SplitString
import com.github.kvnxiao.discord.meirei.utility.ThreadFactory
import net.dv8tion.jda.core.entities.ChannelType
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.reflect.KClass

class CommandListener : ListenerAdapter() {

    companion object {
        const val DEFAULT_THREAD_COUNT = 2
    }

    private val threadPool: ExecutorService = Executors.newFixedThreadPool(System.getProperty(Meirei.DEFAULT_THREAD_ENV_NAME)?.toIntOrNull() ?: DEFAULT_THREAD_COUNT, ThreadFactory())
    private val executor: CommandExecutor = CommandExecutor()
    private val extLoader: ExternalCommandLoader = ExternalCommandLoader()
    private val parser: ICommandParser = CommandParser()
    val registry: ICommandRegistry = CommandRegistry

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
                Meirei.LOGGER.debug("Processing command: $it")
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

    private fun addAnnotatedCommands(instance: Any): Boolean {
        try {
            val commandList = parser.parseAnnotations(instance)
            if (commandList.isEmpty()) return false
            commandList.forEach {
                registry.addCommand(it)
            }
            return true
        } catch (e: InvocationTargetException) {
            Meirei.LOGGER.error("${e.localizedMessage}: Failed to instantiate an object instance of class ${instance::class.java.name}")
            return false
        } catch (e: IllegalAccessException) {
            Meirei.LOGGER.error("${e.localizedMessage}: Failed to access method definition in class ${instance::class.java.name}")
            return false
        }
    }

    fun addCommands(vararg commands: ICommand) {
        commands.forEach { registry.addCommand(it) }
    }

    fun addAnnotatedCommands(vararg classes: Class<*>) = classes.forEach { this.addAnnotatedCommands(it.newInstance()) }

    fun addAnnotatedCommands(vararg ktClasses: KClass<*>) = ktClasses.forEach { this.addAnnotatedCommands(it.java.newInstance()) }

    fun loadExternalCommands(): Boolean {
        val (commands, containers) = extLoader.loadExternalCommands()
        if (commands.isEmpty() && containers.isEmpty()) {
            return false
        }
        commands.forEach { registry.addCommand(it) }
        containers.forEach { this.addAnnotatedCommands(it) }
        return true
    }

}