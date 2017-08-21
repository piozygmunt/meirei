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

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.kvnxiao.discord.meirei.Meirei
import com.github.kvnxiao.discord.meirei.permission.PermissionData
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Bucket4j
import java.util.concurrent.TimeUnit

class RateLimitManager {

    // Map of user id to bucket
    val userManager: Cache<Long, Bucket> = Caffeine.newBuilder()
        .expireAfterAccess(3, TimeUnit.MINUTES)
        .expireAfterWrite(3, TimeUnit.MINUTES)
        .build()

    val guildManager: Cache<Long, Bucket> = Caffeine.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build()

    fun Cache<Long, Bucket>.getOrCreateBucket(id: Long, perms: PermissionData, isGuildLevel: Boolean): Bucket {
        val bucket = this.get(id, { nullBucket(it, isGuildLevel) })
        if (bucket != null) {
            return bucket
        } else {
            val newBucket = Bucket4j.builder().addLimit(perms.tokensPerPeriod, Bandwidth.simple(perms.tokensPerPeriod, java.time.Duration.ofMillis(perms.rateLimitPeriodInMs))).build()
            this.put(id, newBucket)
            return newBucket
        }
    }

    fun isNotUserLimited(userId: Long, perms: PermissionData): Boolean {
        return userManager.getOrCreateBucket(userId, perms, false).tryConsume(1)
    }

    fun isNotRateLimited(guildId: Long, userId: Long, perms: PermissionData): Boolean {
        if (perms.rateLimitOnGuild) {
            return guildManager.getOrCreateBucket(guildId, perms, true).tryConsume(1)
        } else {
            return isNotUserLimited(userId, perms)
        }
    }

    private fun nullBucket(l: Long, isGuildLevel: Boolean): Bucket? {
        Meirei.LOGGER.debug("Creating a new rate-limit bucket for ${if (isGuildLevel) "guild: $l" else "user: $l"}")
        return null
    }
}