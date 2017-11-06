package com.github.kvnxiao.discord.meirei.d4j.command

import com.github.kvnxiao.discord.meirei.command.CommandContext
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

@FunctionalInterface
interface CommandExecutable {

    fun execute(context: CommandContext, event: MessageReceivedEvent)

}