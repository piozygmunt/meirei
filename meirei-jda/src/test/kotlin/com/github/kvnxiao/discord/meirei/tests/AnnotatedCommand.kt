package com.github.kvnxiao.discord.meirei.tests

import com.github.kvnxiao.discord.meirei.command.Command
import com.github.kvnxiao.discord.meirei.jda.command.CommandContext
import net.dv8tion.jda.core.events.message.MessageReceivedEvent

class AnnotatedCommand {

    @Command(
        prefix = "!",
        uniqueName = "test",
        aliases = arrayOf("test", "te", "ann")
    )
    fun command(context: CommandContext, event: MessageReceivedEvent) {
        event.textChannel.sendMessage("annotated command works!").queue()
    }

    @Command(
        uniqueName = "child",
        aliases = arrayOf("child", "test"),
        parentName = "test"
    )
    fun childCommand(context: CommandContext, event: MessageReceivedEvent) {
        event.textChannel.sendMessage("child command works!").queue()
    }

}