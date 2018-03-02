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
package com.github.kvnxiao.discord.meirei.d4j.command

import com.github.kvnxiao.kommandant.command.ExecutionErrorHandler
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.Permissions
import java.util.EnumSet

interface CommandErrorHandler : ExecutionErrorHandler {
    fun onRateLimit(context: CommandContext, event: MessageReceivedEvent)
    fun onMissingPermissions(context: CommandContext, event: MessageReceivedEvent, requiredPerms: EnumSet<Permissions>)
    fun onDirectMessageInvalid(context: CommandContext, event: MessageReceivedEvent)
}
