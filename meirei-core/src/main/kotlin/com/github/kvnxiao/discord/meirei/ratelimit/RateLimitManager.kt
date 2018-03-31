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

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.kvnxiao.discord.meirei.command.permission.PermissionProperties
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Bucket4j
import mu.KotlinLogging
import java.util.concurrent.TimeUnit

private val LOGGER = KotlinLogging.logger { }

class RateLimitManager(
    private val commandId: String
) : DiscordRateLimiter {

    // Map of user id to bucket
    private val userManager: Cache<Long, Bucket> = Caffeine.newBuilder()
        .expireAfterAccess(3, TimeUnit.MINUTES)
        .expireAfterWrite(3, TimeUnit.MINUTES)
        .build()

    // Map of guildId to bucket
    private val guildManager: Cache<Long, Bucket> = Caffeine.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build()

    override fun isNotRateLimited(guildId: Long, userId: Long, permissions: PermissionProperties): Boolean {
        return if (permissions.rateLimitOnGuild) {
            isNotRateLimitedByGuild(guildId, permissions)
        } else {
            isNotRateLimitedByUser(userId, permissions)
        }
    }

    override fun isNotRateLimitedByGuild(guildId: Long, permissions: PermissionProperties): Boolean {
        return guildManager.getOrCreateBucket(guildId, permissions, true).tryConsume(1)
    }

    override fun isNotRateLimitedByUser(userId: Long, permissions: PermissionProperties): Boolean {
        return userManager.getOrCreateBucket(userId, permissions, false).tryConsume(1)
    }

    private fun Cache<Long, Bucket>.getOrCreateBucket(
        id: Long,
        perms: PermissionProperties,
        isGuildLevel: Boolean
    ): Bucket {
        val bucket = this.get(id, { nullBucket(it, isGuildLevel) })
        return if (bucket != null) {
            bucket
        } else {
            val newBucket = Bucket4j.builder().addLimit(perms.tokensPerPeriod, Bandwidth.simple(perms.tokensPerPeriod,
                java.time.Duration.ofMillis(
                    perms.rateLimitPeriodInMs)))
                .build()
            this.put(id, newBucket)
            newBucket
        }
    }

    private fun nullBucket(l: Long, isGuildLevel: Boolean): Bucket? {
        LOGGER.debug { "Creating a new rate-limit bucket for ${if (isGuildLevel) "GUILD:$l" else "USER:$l"}, for command commandId='$commandId'" }
        return null
    }
}
