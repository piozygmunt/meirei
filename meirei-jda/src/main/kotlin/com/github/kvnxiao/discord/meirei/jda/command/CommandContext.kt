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
package com.github.kvnxiao.discord.meirei.jda.command

import com.github.kvnxiao.discord.meirei.command.Context
import com.github.kvnxiao.discord.meirei.command.permission.PermissionProperties
import com.github.kvnxiao.kommandant.command.CommandProperties
import com.github.kvnxiao.kommandant.command.registry.ReadCommandRegistry
import net.dv8tion.jda.core.Permission
import java.util.EnumSet

class CommandContext(
    alias: String,
    args: String?,
    properties: CommandProperties,
    permissions: PermissionProperties,
    permissionLevel: EnumSet<Permission>,
    isDirectMessage: Boolean = false,
    hasBotMention: Boolean = false,
    readOnlyCommandRegistry: ReadCommandRegistry? = null
) : Context<Permission>(alias, args, properties, permissions, permissionLevel, isDirectMessage, hasBotMention,
    readOnlyCommandRegistry) {

    override fun toString(): String {
        return "CommandContext(alias=$alias, args=$args, properties=$properties, permissions=$permissions, permissionLevel=$permissionLevel, isDirectMessage=$isDirectMessage, hasBotMention=$hasBotMention)"
    }
}
