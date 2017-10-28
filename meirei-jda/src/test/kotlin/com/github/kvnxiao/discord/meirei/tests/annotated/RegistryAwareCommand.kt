package com.github.kvnxiao.discord.meirei.tests.annotated

import com.github.kvnxiao.discord.meirei.annotations.Command
import com.github.kvnxiao.discord.meirei.annotations.RegistryAware
import com.github.kvnxiao.discord.meirei.command.CommandContext
import net.dv8tion.jda.core.events.message.MessageReceivedEvent

class RegistryAwareCommand {

    companion object {
        const val PREFIX = "/"
    }

    @Command(
        id = "registry",
        aliases = arrayOf("registry"),
        prefix = PREFIX
    )
    @RegistryAware
    fun commandAlpha(context: CommandContext, event: MessageReceivedEvent) {
        event.channel.sendMessage("This is a registry aware command. all command aliases from registry: ${context.readOnlyCommandRegistry?.getAllCommands()?.joinToString { it.command.id }}").queue()
    }

}