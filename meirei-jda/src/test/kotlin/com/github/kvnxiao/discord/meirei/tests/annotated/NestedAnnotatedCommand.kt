package com.github.kvnxiao.discord.meirei.tests.annotated

import com.github.kvnxiao.discord.meirei.annotations.Command
import com.github.kvnxiao.discord.meirei.annotations.CommandGroup
import com.github.kvnxiao.discord.meirei.command.CommandContext
import net.dv8tion.jda.core.events.message.MessageReceivedEvent

@CommandGroup("test.annotated.nested")
class NestedAnnotatedCommand {

    companion object {
        const val PREFIX = "?"
    }

    @Command(
        id = "alpha",
        aliases = arrayOf("alpha"),
        prefix = PREFIX
    )
    fun commandAlpha(context: CommandContext, event: MessageReceivedEvent) {
        event.channel.sendMessage("This is the first command alpha. args: ${context.args}").queue()
    }

    @Command(
        id = "beta",
        aliases = arrayOf("beta"),
        prefix = PREFIX,
        parentId = "alpha"
    )
    fun commandBeta(context: CommandContext, event: MessageReceivedEvent) {
        event.channel.sendMessage("This is the first child command of alpha, beta. args: ${context.args}").queue()
    }

    @Command(
        id = "charlie",
        aliases = arrayOf("charlie"),
        prefix = PREFIX,
        parentId = "beta"
    )
    fun commandCharlie(context: CommandContext, event: MessageReceivedEvent) {
        event.channel.sendMessage("This is the second child command of alpha and first child command of beta, charlie. args: ${context.args}").queue()
    }

}