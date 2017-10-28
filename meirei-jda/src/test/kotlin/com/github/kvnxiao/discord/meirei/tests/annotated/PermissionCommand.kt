package com.github.kvnxiao.discord.meirei.tests.annotated

import com.github.kvnxiao.discord.meirei.annotations.Command
import com.github.kvnxiao.discord.meirei.annotations.CommandGroup
import com.github.kvnxiao.discord.meirei.annotations.Permissions
import com.github.kvnxiao.discord.meirei.command.CommandContext
import net.dv8tion.jda.core.events.message.MessageReceivedEvent

@CommandGroup("test.annotated.permission")
class PermissionCommand {

    companion object {
        const val PREFIX = "!"
    }

    @Command(
        id = "alpha",
        aliases = arrayOf("alpha"),
        prefix = PREFIX
    )
    @Permissions(reqBotOwner = true)
    fun commandAlpha(context: CommandContext, event: MessageReceivedEvent) {
        event.channel.sendMessage("This is annotated command alpha requiring bot owner privileges. args: ${context.args}").queue()
    }

    @Command(
        id = "beta",
        aliases = arrayOf("beta"),
        prefix = PREFIX
    )
    @Permissions(reqGuildOwner = true)
    fun commandBeta(context: CommandContext, event: MessageReceivedEvent) {
        event.channel.sendMessage("This is annotated command beta requiring guild owner privileges. args: ${context.args}").queue()
    }

    @Command(
        id = "mention",
        aliases = arrayOf("mention"),
        prefix = PREFIX
    )
    @Permissions(reqMention = true)
    fun commandMention(context: CommandContext, event: MessageReceivedEvent) {
        event.channel.sendMessage("This is an annotated command that requires a mention to activate. args: ${context.args}").queue()
    }

}