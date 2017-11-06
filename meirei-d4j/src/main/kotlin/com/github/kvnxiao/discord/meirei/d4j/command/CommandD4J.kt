package com.github.kvnxiao.discord.meirei.d4j.command

import com.github.kvnxiao.discord.meirei.command.DiscordCommand
import com.github.kvnxiao.discord.meirei.permission.PermissionData
import com.github.kvnxiao.discord.meirei.ratelimit.DiscordRateLimiter
import com.github.kvnxiao.discord.meirei.ratelimit.RateLimitManager
import com.github.kvnxiao.discord.meirei.utility.GuildId
import com.github.kvnxiao.discord.meirei.utility.UserId

abstract class CommandD4J(
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