package com.github.kvnxiao.discord.meirei.tests

import com.github.kvnxiao.discord.meirei.command.CommandContext
import com.github.kvnxiao.discord.meirei.command.DiscordCommand
import org.junit.Assert.assertTrue

data class CommandImpl(
    override val id: String,
    override val registryAware: Boolean = false
) : DiscordCommand(id, registryAware) {

    fun execute(context: CommandContext) {
        System.out.println("$id is executing. context.registry is ${context.readOnlyCommandRegistry}")
        assertTrue(registryAware == (context.readOnlyCommandRegistry != null))
    }

}