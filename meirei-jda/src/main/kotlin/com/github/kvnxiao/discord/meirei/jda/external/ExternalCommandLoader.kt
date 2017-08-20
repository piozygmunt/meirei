package com.github.kvnxiao.discord.meirei.jda.external

import com.github.kvnxiao.discord.meirei.Meirei
import com.github.kvnxiao.discord.meirei.command.ICommandContainer
import com.github.kvnxiao.discord.meirei.external.ExternalJarLoader
import com.github.kvnxiao.discord.meirei.jda.command.ICommand

class ExternalCommandLoader {

    fun loadExternalCommands(): Pair<List<ICommand>, List<Class<*>>> {
        val folder: String = System.getProperty(Meirei.DEFAULT_JAR_ENV_NAME) ?: "meirei/jars/"
        val pair: Pair<MutableList<ICommand>, MutableList<Class<*>>> = Pair(mutableListOf(), mutableListOf())
        ExternalJarLoader.loadJars(folder).forEach { k, v ->
            Meirei.LOGGER.debug("Loading $k for commands...")
            v.forEach {
                try {
                    val classInstance: Class<*> = Class.forName(it)
                    if (ICommand::class.java.isAssignableFrom(classInstance)) {
                        // Load class itself as a command
                        val command = createCommandFromClass(classInstance)
                        if (command !== null) pair.first.add(command)
                    } else if (ICommandContainer::class.java.isAssignableFrom(classInstance)) {
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

    fun createCommandFromClass(classInstance: Class<*>): ICommand? {
        val constructors = classInstance.constructors
        constructors
            .map { it.parameterTypes }
            .forEach {
                try {
                    return classInstance.newInstance() as ICommand
                } catch (e: InstantiationException) {
                    Meirei.LOGGER.error("External command class ${classInstance.simpleName} could not be instantiated with a no-args constructor!")
                } catch (e: IllegalAccessException) {
                    Meirei.LOGGER.error("External command class ${classInstance.simpleName} could not be instantiated with a no-args constructor!")
                }
            }
        return null
    }

}