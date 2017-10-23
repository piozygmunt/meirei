package com.github.kvnxiao.discord.meirei.command

import com.github.kvnxiao.discord.meirei.permission.PermissionProperties

data class CommandContext(
    val alias: String,
    val args: String?,
    val properties: CommandProperties,
    val permissions: PermissionProperties,
    val isDirectMessage: Boolean = false,
    val hasBotMention: Boolean = false
)