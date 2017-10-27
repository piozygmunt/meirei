/*
 *   Copyright (C) 2017 Ze Hao Xiao
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
package com.github.kvnxiao.discord.meirei.tests.impl

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