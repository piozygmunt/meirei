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

import com.github.kvnxiao.discord.meirei.command.DiscordCommand
import com.github.kvnxiao.discord.meirei.permission.PermissionData
import com.github.kvnxiao.discord.meirei.ratelimit.DiscordRateLimiter
import com.github.kvnxiao.discord.meirei.ratelimit.RateLimitManager
import com.github.kvnxiao.discord.meirei.utility.GuildId
import com.github.kvnxiao.discord.meirei.utility.UserId

abstract class CommandJDA(
    final override val id: String,
    final override val registryAware: Boolean = false
) : DiscordCommand(id, registryAware), CommandExecutable {

    private val rateLimitManager: DiscordRateLimiter = RateLimitManager(id)

    fun isNotUserLimited(userId: UserId, permissionData: PermissionData): Boolean {
        return rateLimitManager.isNotRateLimitedByUser(userId, permissionData)
    }

    fun isNotRateLimited(guildId: GuildId, userId: UserId, permissionData: PermissionData): Boolean {
        return rateLimitManager.isNotRateLimited(guildId, userId, permissionData)
    }

}