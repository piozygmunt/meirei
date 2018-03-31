/*
 *   Copyright (C) 2017-2018 Ze Hao Xiao
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.github.kvnxiao.discord.meirei.jda.command

import com.github.kvnxiao.kommandant.command.Context
import com.github.kvnxiao.kommandant.command.ExecutableAction
import net.dv8tion.jda.core.events.message.MessageReceivedEvent

/**
 * The Discord specific executable action for Discord commands, which contains the command context and the message
 * received event.
 */
interface DiscordExecutableAction<out T> : ExecutableAction<T> {

    fun execute(context: CommandContext, event: MessageReceivedEvent): T

    override fun execute(context: Context, opt: Array<Any>?): T {
        return execute(context as CommandContext, opt!![0] as MessageReceivedEvent)
    }
}
