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

import com.github.kvnxiao.discord.meirei.command.CommandPackage
import com.github.kvnxiao.discord.meirei.command.CommandProperties
import com.github.kvnxiao.discord.meirei.command.DiscordCommand
import com.github.kvnxiao.discord.meirei.permission.PermissionProperties
import com.github.kvnxiao.discord.meirei.utility.CommandAlias
import com.github.kvnxiao.discord.meirei.utility.CommandId

interface CommandRegistryRead {

    // Get command by prefixed alias
    fun getCommandByAlias(alias: CommandAlias): DiscordCommand?
    fun getCommandById(id: CommandId): DiscordCommand?
    fun getPropertiesById(id: CommandId): CommandProperties?
    fun getPermissionsById(id: CommandId): PermissionProperties?
    fun getSubCommandByAlias(alias: CommandAlias, parentId: CommandId): DiscordCommand?

    // Get list of all commands
    fun getAllCommands(sortById: Boolean = true): List<CommandPackage>
    // Get list of all prefixed aliases
    fun getAllCommandAliases(sorted: Boolean = true): List<CommandAlias>

    // Sub-command info
    fun hasSubCommands(parentId: String): Boolean
    fun getSubCommandRegistry(parentId: String): SubCommandRegistry?

}