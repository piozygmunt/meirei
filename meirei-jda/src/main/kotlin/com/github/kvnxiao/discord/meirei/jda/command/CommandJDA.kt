package com.github.kvnxiao.discord.meirei.jda.command

import com.github.kvnxiao.discord.meirei.command.DiscordCommand

abstract class CommandJDA(
    override val id: String
) : DiscordCommand, CommandExecutable