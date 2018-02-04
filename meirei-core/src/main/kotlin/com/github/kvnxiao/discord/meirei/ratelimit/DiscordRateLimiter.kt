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
package com.github.kvnxiao.discord.meirei.ratelimit

import com.github.kvnxiao.discord.meirei.permission.PermissionData
import com.github.kvnxiao.discord.meirei.utility.GuildId
import com.github.kvnxiao.discord.meirei.utility.UserId

interface DiscordRateLimiter {

    /**
     * Checks if the command is rate limited
     */
    fun isNotRateLimited(guildId: GuildId, userId: UserId, permissions: PermissionData): Boolean

    /**
     * Rate limit level: per guild basis
     * Checks if the command is rate-limited on a guild level for all users
     */
    fun isNotRateLimitedByGuild(guildId: GuildId, permissions: PermissionData): Boolean

    /**
     * Rate limit level: per user basis
     * Checks if the specified user is rate limited on this command
     */
    fun isNotRateLimitedByUser(userId: UserId, permissions: PermissionData): Boolean
}
