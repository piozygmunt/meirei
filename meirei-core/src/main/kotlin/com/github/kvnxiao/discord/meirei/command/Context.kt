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
package com.github.kvnxiao.discord.meirei.command

import com.github.kvnxiao.discord.meirei.command.permission.PermissionProperties
import com.github.kvnxiao.kommandant.command.CommandProperties
import com.github.kvnxiao.kommandant.command.Context
import com.github.kvnxiao.kommandant.command.registry.ReadCommandRegistry
import java.util.EnumSet

open class Context<PERMISSION_LEVEL : Enum<PERMISSION_LEVEL>>(
    alias: String,
    args: String?,
    properties: CommandProperties,
    val permissions: PermissionProperties,
    val permissionLevel: EnumSet<PERMISSION_LEVEL>,
    val isDirectMessage: Boolean = false,
    val hasBotMention: Boolean = false,
    val readOnlyCommandRegistry: ReadCommandRegistry? = null
) : Context(alias, args, properties) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is com.github.kvnxiao.discord.meirei.command.Context<*>) return false
        if (!super.equals(other)) return false

        if (permissions != other.permissions) return false
        if (permissionLevel != other.permissionLevel) return false
        if (isDirectMessage != other.isDirectMessage) return false
        if (hasBotMention != other.hasBotMention) return false
        if (readOnlyCommandRegistry != other.readOnlyCommandRegistry) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + permissions.hashCode()
        result = 31 * result + permissionLevel.hashCode()
        result = 31 * result + isDirectMessage.hashCode()
        result = 31 * result + hasBotMention.hashCode()
        result = 31 * result + (readOnlyCommandRegistry?.hashCode() ?: 0)
        return result
    }
}
