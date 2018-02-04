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
package com.github.kvnxiao.discord.meirei.annotations

import com.github.kvnxiao.discord.meirei.permission.PermissionDefaults

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Permissions(
    /**
     * @see[PermissionDefaults.ALLOW_DIRECT_MSGING]
     */
    val allowDm: Boolean = PermissionDefaults.ALLOW_DIRECT_MSGING,
    /**
     * @see[PermissionDefaults.FORCE_DIRECT_MSGING]
     */
    val forceDm: Boolean = PermissionDefaults.FORCE_DIRECT_MSGING,
    /**
     * @see[PermissionDefaults.FORCE_DIRECT_REPLY]
     */
    val forceDmReply: Boolean = PermissionDefaults.FORCE_DIRECT_REPLY,
    /**
     * @see[PermissionDefaults.REMOVE_CALL_MSG]
     */
    val removeCallMsg: Boolean = PermissionDefaults.REMOVE_CALL_MSG,
    /**
     * @see[PermissionDefaults.RATE_LIMIT_PERIOD_MS]
     */
    val rateLimitPeriodMs: Long = PermissionDefaults.RATE_LIMIT_PERIOD_MS,
    /**
     * @see[PermissionDefaults.TOKENS_PER_PERIOD]
     */
    val tokensPerPeriod: Long = PermissionDefaults.TOKENS_PER_PERIOD,
    /**
     * @see[PermissionDefaults.RATE_LIMIT_ON_GUILD]
     */
    val rateLimitOnGuild: Boolean = PermissionDefaults.RATE_LIMIT_ON_GUILD,
    /**
     * @see[PermissionDefaults.REQUIRE_BOT_OWNER]
     */
    val reqBotOwner: Boolean = PermissionDefaults.REQUIRE_BOT_OWNER,
    /**
     * @see[PermissionDefaults.REQUIRE_GUILD_OWNER]
     */
    val reqGuildOwner: Boolean = PermissionDefaults.REQUIRE_GUILD_OWNER,
    /**
     * @see[PermissionDefaults.REQUIRE_MENTION]
     */
    val reqMention: Boolean = PermissionDefaults.REQUIRE_MENTION
)
