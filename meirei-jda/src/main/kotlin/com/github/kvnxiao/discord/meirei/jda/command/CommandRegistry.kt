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

class CommandRegistry : ICommandRegistry {

    private val uniqueNameMap: MutableMap<String, ICommand> = mutableMapOf()
    private val aliasMap: MutableMap<String, String> = mutableMapOf()

    override fun addCommand(command: ICommand): Boolean {
        // Check for prefix + alias clash and unique name clashes
        if (!validateAliases(command.properties.prefix, command.properties.aliases)) {
            Meirei.LOGGER.warn("Could not register command '$command' with prefix '${command.properties.prefix}' due to clashing with existing command prefix + aliases!")
            return false
        } else if (uniqueNameMap.containsKey(command.properties.uniqueName)) {
            Meirei.LOGGER.warn("Could not register command '$command' with prefix '${command.properties.prefix}' due to the unique name already existing in the registry!")
            return false
        }
        // TODO: Custom prefixes

        // Insert command into aliasMap
        command.properties.aliases.forEach {
            aliasMap.put(command.properties.prefix + it, command.properties.uniqueName)
        }
        // Insert command into unique commands map
        uniqueNameMap.put(command.properties.uniqueName, command)

        Meirei.LOGGER.debug("Registered command '$command': prefix '${command.properties.prefix}', aliases '${command.properties.aliases}'")
        return true
    }

    override fun deleteCommand(command: ICommand): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCommandByAlias(alias: String): ICommand? {
        val uniqueName = aliasMap[alias]
        return if (uniqueName != null) uniqueNameMap[uniqueName] else null
    }

    override fun enableCommand() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun disableCommand() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isAliasExist(input: String): Boolean {
        return aliasMap.containsKey(input)
    }

    override fun validateAliases(prefix: String, aliases: Set<String>): Boolean {
        return aliases.none { aliasMap.containsKey(prefix + it) }
    }

}