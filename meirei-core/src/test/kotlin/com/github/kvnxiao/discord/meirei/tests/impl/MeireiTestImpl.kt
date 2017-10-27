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
package com.github.kvnxiao.discord.meirei.tests.impl

import com.github.kvnxiao.discord.meirei.Meirei
import com.github.kvnxiao.discord.meirei.command.CommandContext
import com.github.kvnxiao.discord.meirei.utility.splitString

class MeireiTestImpl : Meirei() {

    fun execute(input: String): Boolean {
        val (alias, args) = splitString(input)

        if (alias != null) {
            val command = registry.getCommandByAlias(alias) as CommandImpl?
            if (command != null) {
                val properties = registry.getPropertiesById(command.id)
                val permissions = registry.getPermissionsById(command.id)
                if (properties != null && permissions != null) {
                    // Execute command
                    val context = CommandContext(alias, args, properties, permissions,
                        readOnlyCommandRegistry = if (command.registryAware) registry else null)
                    return executeCommand(command, context)
                }
            }
        }
        return false
    }

    private fun executeCommand(command: CommandImpl, context: CommandContext): Boolean {
        if (!context.properties.isDisabled) {
            // Check sub-commands
            val args = context.args
            if (args != null && registry.hasSubCommands(command.id)) {
                // Try getting a sub-command from the args
                val (subAlias, subArgs) = splitString(args)
                if (subAlias != null) {
                    val subCommand = registry.getSubCommandByAlias(subAlias, command.id) as CommandImpl?
                    if (subCommand != null) {
                        val subProperties = registry.getPropertiesById(subCommand.id)
                        val subPermissions = registry.getPermissionsById(subCommand.id)
                        if (subProperties != null && subPermissions != null) {
                            // Execute sub-command
                            val subContext = CommandContext(subAlias, subArgs, subProperties, subPermissions,
                                readOnlyCommandRegistry = if (subCommand.registryAware) registry else null)
                            // Execute parent-command if the boolean value is true
                            if (context.properties.execWithSubCommands) command.execute(context)
                            return executeCommand(subCommand, subContext)
                        }
                    }
                }
            } else {
                command.execute(context)
                return true
            }
        }
        return false
    }

}