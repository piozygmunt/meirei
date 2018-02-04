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
package com.github.kvnxiao.discord.meirei.permission

object PermissionDefaults {

    /**
     * The setting for whether the command requires a bot mention to activate, defaults to false.
     */
    const val REQUIRE_MENTION = false

    /**
     * The setting for whether the command can be activated through direct message to the bot, defaults to false.
     */
    const val ALLOW_DIRECT_MSGING = false

    /**
     * The setting for whether the command must be activated through direct message with the bot, defaults to false.
     */
    const val FORCE_DIRECT_MSGING = false

    /**
     * The setting for whether the command replies are forced as a direct message, defaults to false.
     */
    const val FORCE_DIRECT_REPLY = false

    /**
     * The setting for whether the message used to activate the command should be removed post-activation, defaults to false.
     */
    const val REMOVE_CALL_MSG = false

    /**
     * The time period for rate limiting a command in milliseconds, defaults to 1000 ms or 1 second.
     */
    const val RATE_LIMIT_PERIOD_MS: Long = 1000

    /**
     * The number of valid tokens or calls per rate limit period for a command, defaults to 3 calls per second.
     */
    const val TOKENS_PER_PERIOD: Long = 3

    /**
     * The setting for whether rate limiting is per guild or per user, defaults to false meaning per user.
     */
    const val RATE_LIMIT_ON_GUILD = false

    /**
     * The setting for whether the command can only be issued by the guild owner, defaults to false.
     */
    const val REQUIRE_GUILD_OWNER = false

    /**
     * The setting for whether the command can only be issued by the bot owner, defaults to false.
     */
    const val REQUIRE_BOT_OWNER = false
}
