package com.github.kvnxiao.discord.meirei.command

import com.github.kvnxiao.discord.meirei.permission.PermissionProperties

data class CommandPackage(
    val command: DiscordCommand,
    val commandProperties: CommandProperties,
    val permissionProperties: PermissionProperties
)