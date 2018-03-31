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
package com.github.kvnxiao.discord.meirei.command.permission

open class PermissionProperties(
    val allowDmFromSender: Boolean = PermissionDefaults.ALLOW_DIRECT_MSGING,
    val forceDmFromSender: Boolean = PermissionDefaults.FORCE_DIRECT_MSGING,
    val forceDmReply: Boolean = PermissionDefaults.FORCE_DIRECT_REPLY,
    val removeCallMsg: Boolean = PermissionDefaults.REMOVE_CALL_MSG,
    val rateLimitPeriodInMs: Long = PermissionDefaults.RATE_LIMIT_PERIOD_MS,
    val tokensPerPeriod: Long = PermissionDefaults.TOKENS_PER_PERIOD,
    val rateLimitOnGuild: Boolean = PermissionDefaults.RATE_LIMIT_ON_GUILD,
    val reqGuildOwner: Boolean = PermissionDefaults.REQUIRE_GUILD_OWNER,
    val reqBotOwner: Boolean = PermissionDefaults.REQUIRE_BOT_OWNER,
    val requireMention: Boolean = PermissionDefaults.REQUIRE_MENTION
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PermissionProperties) return false

        if (allowDmFromSender != other.allowDmFromSender) return false
        if (forceDmFromSender != other.forceDmFromSender) return false
        if (forceDmReply != other.forceDmReply) return false
        if (removeCallMsg != other.removeCallMsg) return false
        if (rateLimitPeriodInMs != other.rateLimitPeriodInMs) return false
        if (tokensPerPeriod != other.tokensPerPeriod) return false
        if (rateLimitOnGuild != other.rateLimitOnGuild) return false
        if (reqGuildOwner != other.reqGuildOwner) return false
        if (reqBotOwner != other.reqBotOwner) return false
        if (requireMention != other.requireMention) return false

        return true
    }

    override fun hashCode(): Int {
        var result = allowDmFromSender.hashCode()
        result = 31 * result + forceDmFromSender.hashCode()
        result = 31 * result + forceDmReply.hashCode()
        result = 31 * result + removeCallMsg.hashCode()
        result = 31 * result + rateLimitPeriodInMs.hashCode()
        result = 31 * result + tokensPerPeriod.hashCode()
        result = 31 * result + rateLimitOnGuild.hashCode()
        result = 31 * result + reqGuildOwner.hashCode()
        result = 31 * result + reqBotOwner.hashCode()
        result = 31 * result + requireMention.hashCode()
        return result
    }

    override fun toString(): String {
        return "PermissionProperties(allowDmFromSender=$allowDmFromSender, forceDmFromSender=$forceDmFromSender, forceDmReply=$forceDmReply, removeCallMsg=$removeCallMsg, rateLimitPeriodInMs=$rateLimitPeriodInMs, tokensPerPeriod=$tokensPerPeriod, rateLimitOnGuild=$rateLimitOnGuild, reqGuildOwner=$reqGuildOwner, reqBotOwner=$reqBotOwner, requireMention=$requireMention)"
    }
}
