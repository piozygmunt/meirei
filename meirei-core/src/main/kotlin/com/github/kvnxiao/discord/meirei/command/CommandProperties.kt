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
package com.github.kvnxiao.discord.meirei.command

import com.github.kvnxiao.discord.meirei.utility.CommandAlias
import com.github.kvnxiao.discord.meirei.utility.CommandId

data class CommandProperties(
    // Required properties for every command
    val id: CommandId,
    val aliases: Set<CommandAlias> = setOf(id),
    val prefix: String = CommandDefaults.PREFIX,
    // Metadata
    val description: String = CommandDefaults.NO_DESCRIPTION,
    val usage: String = CommandDefaults.NO_USAGE,
    // Command settings
    val execWithSubCommands: Boolean = CommandDefaults.EXEC_ALONGSIDE_SUBCOMMANDS,
    val isDisabled: Boolean = CommandDefaults.IS_DISABLED,
    // Parent command id
    val parentId: CommandId = CommandDefaults.PARENT_ID
)
