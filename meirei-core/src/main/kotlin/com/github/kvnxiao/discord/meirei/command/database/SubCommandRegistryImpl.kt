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
import com.github.kvnxiao.discord.meirei.command.CommandProperties
import com.github.kvnxiao.discord.meirei.utility.CommandAlias
import com.github.kvnxiao.discord.meirei.utility.CommandId

data class SubCommandRegistryImpl(
    val id: String
) : SubCommandRegistry {
    private val aliasIdMap: MutableMap<CommandAlias, CommandId> = mutableMapOf()
    private val subCommandIds: MutableSet<CommandId> = mutableSetOf()

    override fun getSubCommandIdByAlias(alias: CommandAlias): CommandId? = aliasIdMap[alias]

    override fun getAllSubCommandIds(sortById: Boolean): List<CommandId> {
        return if (sortById) subCommandIds.sorted().toList() else subCommandIds.toList()
    }

    override fun getAllSubCommandAliases(sorted: Boolean): List<CommandAlias> {
        return if (sorted) aliasIdMap.keys.sorted().toList() else aliasIdMap.keys.toList()
    }

    override fun addSubCommand(subCommandProperties: CommandProperties, parentId: CommandId): Boolean {
        // Validate aliases
        if (!validateAliases(subCommandProperties.aliases)) {
            Meirei.LOGGER.warn("Could not register sub-command ${subCommandProperties.id} to $parentId due to it clashing with existing sub-command aliases.")
            return false
        } else if (subCommandIds.contains(subCommandProperties.id)) {
            Meirei.LOGGER.warn("Could not register sub-command ${subCommandProperties.id} to $parentId due to the unique id already existing in the sub-command registry.")
            return false
        }

        // Map all aliases of the sub-command to the sub-command id
        subCommandProperties.aliases.forEach {
            aliasIdMap.put(it, subCommandProperties.id)
        }
        // Add sub-command id to known set
        subCommandIds.add(subCommandProperties.id)

        Meirei.LOGGER.debug("Registered sub-command '${subCommandProperties.id}' to '$parentId'")
        return true
    }

    override fun removeSubCommand(subCommandProperties: CommandProperties): Boolean {
        return if (subCommandIds.contains(subCommandProperties.id)) {
            // Remove aliases from aliases map
            subCommandProperties.aliases.forEach {
                aliasIdMap.remove(it)
            }
            // Remove id from sub-commands map
            subCommandIds.remove(subCommandProperties.id)
            true
        } else {
            false
        }
    }

    override fun containsCommands(): Boolean {
        return aliasIdMap.isNotEmpty()
    }

    private fun validateAliases(aliases: Set<String>): Boolean {
        return aliases.none { aliasIdMap.containsKey(it) }
    }
}
