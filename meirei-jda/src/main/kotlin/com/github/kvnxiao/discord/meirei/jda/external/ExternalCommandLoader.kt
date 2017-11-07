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
package com.github.kvnxiao.discord.meirei.jda.external

import com.github.kvnxiao.discord.meirei.Meirei
import com.github.kvnxiao.discord.meirei.command.CommandContainer
import com.github.kvnxiao.discord.meirei.external.JarLoader
import com.github.kvnxiao.discord.meirei.jda.command.CommandJDA

class ExternalCommandLoader {

    fun loadExternalCommands(): Pair<List<CommandJDA>, List<Class<*>>> {
        val folder: String = System.getProperty(Meirei.ENV_COMMAND_JAR_FOLDER) ?: "meirei/jars/"
        val pair: Pair<MutableList<CommandJDA>, MutableList<Class<*>>> = Pair(mutableListOf(), mutableListOf())
        JarLoader().loadJarFiles(folder).forEach { k, v ->
            Meirei.LOGGER.debug("Loading $k for commands...")
            v.forEach {
                try {
                    val classInstance: Class<*> = Class.forName(it)
                    if (CommandJDA::class.java.isAssignableFrom(classInstance)) {
                        // Load class itself as a command
                        val command = createCommandFromClass(classInstance)
                        if (command !== null) pair.first.add(command)
                    } else if (CommandContainer::class.java.isAssignableFrom(classInstance)) {
                        // Parse annotations for commands
                        pair.second.add(classInstance)
                    }
                } catch (ignored: ClassNotFoundException) {
                    // Ignore errors for class not found.
                }
            }
        }
        return pair
    }

    fun createCommandFromClass(classInstance: Class<*>): CommandJDA? {
        val constructors = classInstance.constructors
        constructors
            .map { it.parameterTypes }
            .forEach {
                try {
                    return classInstance.newInstance() as CommandJDA
                } catch (e: InstantiationException) {
                    Meirei.LOGGER.error("External command class ${classInstance.simpleName} could not be instantiated with a no-args constructor!")
                } catch (e: IllegalAccessException) {
                    Meirei.LOGGER.error("External command class ${classInstance.simpleName} could not be instantiated with a no-args constructor!")
                }
            }
        return null
    }
}
