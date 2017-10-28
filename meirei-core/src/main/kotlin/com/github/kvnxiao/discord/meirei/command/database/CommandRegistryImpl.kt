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
package com.github.kvnxiao.discord.meirei.command.database

import com.github.kvnxiao.discord.meirei.Meirei
import com.github.kvnxiao.discord.meirei.command.CommandDefaults
import com.github.kvnxiao.discord.meirei.command.CommandProperties
import com.github.kvnxiao.discord.meirei.command.DiscordCommand
import com.github.kvnxiao.discord.meirei.permission.PermissionProperties
import com.github.kvnxiao.discord.meirei.utility.CommandAlias
import com.github.kvnxiao.discord.meirei.utility.CommandId

class CommandRegistryImpl : CommandRegistry() {

    // Map for command executors
    private val idExecutorMap: MutableMap<CommandId, DiscordCommand> = mutableMapOf()
    private val aliasIdMap: MutableMap<CommandAlias, CommandId> = mutableMapOf()

    // Map for command properties
    private val idPropertiesMap: MutableMap<CommandId, CommandProperties> = mutableMapOf()

    // Map for permission properties
    private val idPermissionsMap: MutableMap<CommandId, PermissionProperties> = mutableMapOf()

    // Disabled commands
    private val disabledCommands: MutableSet<CommandId> = mutableSetOf()

    private val parentIdSubCommandsMap: MutableMap<CommandId, SubCommandRegistry> = mutableMapOf()

    override fun addCommand(command: DiscordCommand, commandProperties: CommandProperties, permissionProperties: PermissionProperties): Boolean {
        // Check for prefix + alias clash and unique id clashes
        if (!validateAliases(commandProperties.prefix, commandProperties.aliases)) {
            Meirei.LOGGER.warn("Could not register command '$command' with prefix '${commandProperties.prefix}' due to it clashing with existing aliases.")
            return false
        } else if (idExecutorMap.containsKey(commandProperties.id)) {
            Meirei.LOGGER.warn("Could not register command '$command' with prefix '${commandProperties.prefix}' due to the unique id already existing in the registry.")
            return false
        }
        // TODO: custom prefix validation

        // Insert command into alias->id map
        commandProperties.aliases.forEach {
            aliasIdMap.put(commandProperties.prefix + it, commandProperties.id)
        }

        // Insert command into id->executor map
        idExecutorMap.put(commandProperties.id, command)
        // Insert properties into id->prop map
        idPropertiesMap.put(commandProperties.id, commandProperties)
        idPermissionsMap.put(commandProperties.id, permissionProperties)

        Meirei.LOGGER.debug("Registered command '$command': prefix '${commandProperties.prefix}', aliases '${commandProperties.aliases}'")
        return true
    }

    override fun deleteCommand(id: CommandId): Boolean {
        // Remove from aliases map
        val properties = idPropertiesMap[id] ?: return false
        properties.aliases.forEach {
            aliasIdMap.remove(properties.prefix + it)
        }

        // Remove from properties maps
        idPropertiesMap.remove(id)
        idPermissionsMap.remove(id)

        // Remove from executor map
        idExecutorMap.remove(id)
        return true
    }

    override fun addSubCommand(subCommand: DiscordCommand, commandProperties: CommandProperties, permissionProperties: PermissionProperties, parentId: CommandId): Boolean {
        // Fix commandProperties if it is missing parent-id link
        val properties = if (commandProperties.parentId == CommandDefaults.PARENT_ID) {
            CommandProperties(
                commandProperties.id,
                commandProperties.aliases,
                commandProperties.prefix,
                commandProperties.description,
                commandProperties.usage,
                commandProperties.execWithSubCommands,
                commandProperties.isDisabled,
                parentId
            )
        } else {
            commandProperties
        }

        // Update sub-command registry
        val subCommandRegistry = parentIdSubCommandsMap.getOrPut(parentId, { SubCommandRegistryImpl(subCommand.id) })
        val success = subCommandRegistry.addSubCommand(properties, parentId)
        return if (success) {
            // Add sub-command to main registry
            idExecutorMap.put(properties.id, subCommand)
            idPropertiesMap.put(properties.id, properties)
            idPermissionsMap.put(properties.id, permissionProperties)
            true
        } else {
            parentIdSubCommandsMap.remove(parentId)
            false
        }
    }

    override fun removeSubCommand(subCommandId: CommandId, parentId: CommandId): Boolean {
        val subCommandRegistry = parentIdSubCommandsMap[parentId] ?: return false
        val subCommandProperties = idPropertiesMap[subCommandId] ?: return false

        val success = subCommandRegistry.removeSubCommand(subCommandProperties)
        if (success) {
            // Remove sub-command info from main registry
            idExecutorMap.remove(subCommandId)
            idPropertiesMap.remove(subCommandId)
            idPropertiesMap.remove(subCommandId)
            if (subCommandRegistry.getAllSubCommandIds().isEmpty()) {
                parentIdSubCommandsMap.remove(parentId)
            }
            return true
        }
        return false
    }

    override fun getCommandByAlias(alias: CommandAlias): DiscordCommand? {
        val id = aliasIdMap[alias]
        return if (id != null) idExecutorMap[id] else null
    }

    override fun getCommandById(id: CommandId): DiscordCommand? {
        return idExecutorMap[id]
    }

    override fun getPropertiesById(id: CommandId): CommandProperties? {
        return idPropertiesMap[id]
    }

    override fun getPermissionsById(id: CommandId): PermissionProperties? {
        return idPermissionsMap[id]
    }

    override fun getAllCommands(sortById: Boolean): List<DiscordCommand> {
        return if (sortById) idExecutorMap.values.sortedBy { it.id }.toList() else idExecutorMap.values.toList()
    }

    override fun getAllCommandAliases(sorted: Boolean): List<String> {
        return if (sorted) aliasIdMap.keys.sorted().toList() else aliasIdMap.keys.toList()
    }

    override fun getSubCommandRegistry(parentId: String): SubCommandRegistry? {
        return parentIdSubCommandsMap[parentId]
    }

    override fun getSubCommandByAlias(alias: CommandAlias, parentId: CommandId): DiscordCommand? {
        val subCommandRegistry = getSubCommandRegistry(parentId)
        val subCommandId = subCommandRegistry?.getSubCommandIdByAlias(alias)
        return if (subCommandId != null) {
            getCommandById(subCommandId)
        } else {
            null
        }
    }

    override fun enableCommand(id: CommandId) {
        disabledCommands.remove(id)
    }

    override fun disableCommand(id: CommandId) {
        disabledCommands.add(id)
    }

    override fun hasSubCommands(parentId: String): Boolean {
        return parentIdSubCommandsMap[parentId]?.containsCommands() ?: false
    }

    private fun validateAliases(prefix: String, aliases: Set<String>): Boolean {
        return aliases.none { aliasIdMap.containsKey(prefix + it) }
    }
}