package com.github.kvnxiao.discord.meirei.command.database

import com.github.kvnxiao.discord.meirei.command.CommandProperties
import com.github.kvnxiao.discord.meirei.command.DiscordCommand
import com.github.kvnxiao.discord.meirei.permission.PermissionProperties

interface CommandRegistry {

    // Add and delete commands
    fun addCommand(command: DiscordCommand, commandProperties: CommandProperties, permissionProperties: PermissionProperties): Boolean

    fun deleteCommand(id: String): Boolean

    fun getCommandByAlias(alias: String): DiscordCommand?

    fun getAllCommands(sortById: Boolean = true): List<DiscordCommand>

    fun getAllCommandAliases(sorted: Boolean = true): List<String>

    fun enableCommand(id: String)

    fun disableCommand(id: String)

}