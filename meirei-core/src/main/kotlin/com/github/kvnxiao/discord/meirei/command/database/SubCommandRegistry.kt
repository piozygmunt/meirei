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
package com.github.kvnxiao.discord.meirei.command.database

import com.github.kvnxiao.discord.meirei.command.CommandProperties
import com.github.kvnxiao.discord.meirei.utility.CommandAlias
import com.github.kvnxiao.discord.meirei.utility.CommandId

interface SubCommandRegistry {

    // Get sub-command by its id
    fun getSubCommandIdByAlias(alias: CommandAlias): CommandId?

    // Get list of all sub-command ids
    fun getAllSubCommandIds(sortById: Boolean = true): List<CommandId>
    // Get list of all sub-command aliases
    fun getAllSubCommandAliases(sorted: Boolean = true): List<CommandAlias>

    // Adding and removing sub-commands
    fun addSubCommand(subCommandProperties: CommandProperties, parentId: CommandId): Boolean
    fun removeSubCommand(subCommandProperties: CommandProperties): Boolean

    // Check if the registry is not empty
    fun containsCommands(): Boolean
}
