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
package com.github.kvnxiao.discord.meirei.jda.tests.impl

import com.github.kvnxiao.discord.meirei.Meirei
import com.github.kvnxiao.discord.meirei.command.CommandContext
import com.github.kvnxiao.discord.meirei.command.database.CommandRegistry
import com.github.kvnxiao.discord.meirei.utility.SplitString.Companion.splitString

class MeireiTestImpl(registry: CommandRegistry) : Meirei(registry, CommandParserImpl()) {

    fun process(input: String): Boolean {
        val (alias, args) = splitString(input)

        alias.let {
            val command = registry.getCommandByAlias(it) as CommandImpl?
            command?.let {
                val properties = registry.getPropertiesById(it.id)
                val permissions = registry.getPermissionsById(it.id)
                if (properties != null && permissions != null) {
                    // Execute command
                    val context = CommandContext(alias, args, properties, permissions,
                        readOnlyCommandRegistry = if (it.registryAware) registry else null)
                    Meirei.LOGGER.debug { "Processing command: ${it.id}" }
                    return execute(it, context)
                }
            }
        }
        return false
    }

    private fun execute(command: CommandImpl, context: CommandContext): Boolean {
        if (!context.properties.isDisabled) {
            // Check sub-commands
            val args = context.args
            if (args != null && registry.hasSubCommands(command.id)) {
                // Try getting a sub-command from the args
                val (subAlias, subArgs) = splitString(args)
                val subCommand = registry.getSubCommandByAlias(subAlias, command.id) as CommandImpl?
                if (subCommand != null) {
                    val subProperties = registry.getPropertiesById(subCommand.id)
                    val subPermissions = registry.getPermissionsById(subCommand.id)
                    if (subProperties != null && subPermissions != null) {
                        // Execute sub-command
                        val subContext = CommandContext(subAlias, subArgs, subProperties, subPermissions,
                            context.isDirectMessage, context.hasBotMention, if (subCommand.registryAware) registry else null)
                        // Execute parent-command if the boolean value is true
                        if (context.properties.execWithSubCommands) command.execute(context)
                        return execute(subCommand, subContext)
                    }
                }
            }
            return executeCommand(command, context)
        }
        return false
    }

    private fun executeCommand(command: CommandImpl, context: CommandContext): Boolean {
        return command.execute(context)
    }

    override fun registerEventListeners(client: Any) = Unit
}
