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
import com.github.kvnxiao.discord.meirei.ratelimit.DiscordRateLimiter
import com.github.kvnxiao.discord.meirei.ratelimit.RateLimitManager
import com.github.kvnxiao.kommandant.command.CommandPackage
import com.github.kvnxiao.kommandant.command.CommandProperties
import com.github.kvnxiao.kommandant.command.ExecutableAction
import com.github.kvnxiao.kommandant.command.ExecutionErrorHandler
import java.util.EnumSet

open class DiscordCommandPackage<PERMISSION_LEVEL : Enum<PERMISSION_LEVEL>>(
    executable: ExecutableAction<Any?>,
    properties: CommandProperties,
    val permissions: PermissionProperties,
    errorHandler: ExecutionErrorHandler,
    val permissionLevel: EnumSet<PERMISSION_LEVEL>,
    val isRegistryAware: Boolean
) : CommandPackage<Any?>(executable, properties, errorHandler) {

    private val rateLimitManager: DiscordRateLimiter = RateLimitManager(properties.id)

    fun isNotUserLimited(userId: Long, permissions: PermissionProperties): Boolean {
        return rateLimitManager.isNotRateLimitedByUser(userId, permissions)
    }

    fun isNotRateLimited(guildId: Long, userId: Long, permissions: PermissionProperties): Boolean {
        return rateLimitManager.isNotRateLimited(guildId, userId, permissions)
    }

    override fun toString(): String {
        return "Command(id=${properties.id}, prefix=${properties.prefix}, aliases=${properties.aliases}, parentId=${properties.parentId})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DiscordCommandPackage<*>) return false
        if (!super.equals(other)) return false

        if (permissions != other.permissions) return false
        if (permissionLevel != other.permissionLevel) return false
        if (isRegistryAware != other.isRegistryAware) return false
        if (rateLimitManager != other.rateLimitManager) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + permissions.hashCode()
        result = 31 * result + permissionLevel.hashCode()
        result = 31 * result + isRegistryAware.hashCode()
        result = 31 * result + rateLimitManager.hashCode()
        return result
    }
}
