package com.github.kvnxiao.discord.meirei.command.database

import com.github.kvnxiao.discord.meirei.Meirei
import com.github.kvnxiao.discord.meirei.command.CommandProperties
import com.github.kvnxiao.discord.meirei.command.DiscordCommand
import com.github.kvnxiao.discord.meirei.permission.PermissionProperties
import com.github.kvnxiao.discord.meirei.utility.CommandAlias
import com.github.kvnxiao.discord.meirei.utility.CommandId

class CommandRegistryImpl : CommandRegistry {

    // Map for command executors
    private val idExecutorMap: MutableMap<CommandId, DiscordCommand> = mutableMapOf()
    private val aliasIdMap: MutableMap<CommandAlias, CommandId> = mutableMapOf()

    // Map for command properties
    private val idPropertiesMap: MutableMap<CommandId, CommandProperties> = mutableMapOf()

    // Map for permission properties
    private val idPermissionsMap: MutableMap<CommandId, PermissionProperties> = mutableMapOf()

    private val disabledCommands: MutableSet<CommandId> = mutableSetOf()

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

    override fun deleteCommand(id: String): Boolean {
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

    override fun getCommandByAlias(alias: String): DiscordCommand? {
        val id = aliasIdMap[alias]
        return if (id != null) idExecutorMap[id] else null
    }

    override fun getAllCommands(sortById: Boolean): List<DiscordCommand> {
        return if (sortById) idExecutorMap.values.sortedBy { it.id }.toList() else idExecutorMap.values.toList()
    }

    override fun getAllCommandAliases(sorted: Boolean): List<String> {
        return if (sorted) aliasIdMap.keys.sorted().toList() else aliasIdMap.keys.toList()
    }

    override fun enableCommand(id: String) {
        disabledCommands.remove(id)
    }

    override fun disableCommand(id: String) {
        disabledCommands.add(id)
    }

    private fun validateAliases(prefix: String, aliases: Set<String>): Boolean {
        return aliases.none { aliasIdMap.containsKey(prefix + it) }
    }
}