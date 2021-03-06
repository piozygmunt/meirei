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
package com.github.kvnxiao.discord.meirei.d4j.command

import com.github.kvnxiao.discord.meirei.command.CommandDefaults
import com.github.kvnxiao.discord.meirei.command.DiscordCommand
import com.github.kvnxiao.discord.meirei.permission.PermissionData
import com.github.kvnxiao.discord.meirei.ratelimit.DiscordRateLimiter
import com.github.kvnxiao.discord.meirei.ratelimit.RateLimitManager
import com.github.kvnxiao.discord.meirei.utility.GuildId
import com.github.kvnxiao.discord.meirei.utility.UserId

abstract class CommandD4J(
    final override val id: String,
    final override val registryAware: Boolean = CommandDefaults.IS_REGISTRY_AWARE
) : DiscordCommand(id, registryAware), CommandExecutable {

    private val rateLimitManager: DiscordRateLimiter = RateLimitManager(id)

    fun isNotUserLimited(userId: UserId, permissionData: PermissionData): Boolean {
        return rateLimitManager.isNotRateLimitedByUser(userId, permissionData)
    }

    fun isNotRateLimited(guildId: GuildId, userId: UserId, permissionData: PermissionData): Boolean {
        return rateLimitManager.isNotRateLimited(guildId, userId, permissionData)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CommandD4J) return false

        if (id != other.id) return false
        if (registryAware != other.registryAware) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + registryAware.hashCode()
        return result
    }

    override fun toString(): String {
        return "(id='$id', registryAware=$registryAware)"
    }
}
