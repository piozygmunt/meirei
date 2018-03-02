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
package com.github.kvnxiao.discord.meirei.d4j.command.parser

import com.github.kvnxiao.discord.meirei.command.DiscordCommandPackage
import com.github.kvnxiao.discord.meirei.command.parser.AnnotationParser
import com.github.kvnxiao.discord.meirei.d4j.command.CommandContext
import com.github.kvnxiao.discord.meirei.d4j.command.DefaultErrorHandler
import com.github.kvnxiao.discord.meirei.d4j.command.DiscordExecutableAction
import com.github.kvnxiao.discord.meirei.d4j.command.annotations.PermissionLevel
import com.github.kvnxiao.discord.meirei.d4j.command.permission.PermissionLevelDefaults
import com.github.kvnxiao.kommandant.command.CommandPackage
import com.github.kvnxiao.kommandant.command.ExecutableAction
import com.github.kvnxiao.kommandant.command.annotations.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.Permissions
import java.lang.reflect.Method
import java.util.EnumSet

class DiscordCommandParser : AnnotationParser() {

    override fun createCommand(method: Method, commandAnn: Command, id: String, globalPrefix: String?, instance: Any): CommandPackage<*> {
        return DiscordCommandPackage(
            this.createExecutable(method, instance),
            this.createProperties(method, id, globalPrefix, commandAnn),
            this.createPermissionProperties(method, method.getPermissions()),
            this.createErrorHandler(method, instance, { DefaultErrorHandler() }),
            this.createPermissionLevels(method),
            method.isRegistryAware()
        )
    }

    private fun createPermissionLevels(method: Method): EnumSet<Permissions> {
        val permissionLevel = method.getPermissionLevel()

        return if (permissionLevel != null) {
            val level: EnumSet<Permissions> = EnumSet.noneOf(Permissions::class.java)
            level.addAll(permissionLevel.level)
            level
        } else {
            PermissionLevelDefaults.DEFAULT_PERMS_RW
        }
    }

    override fun createExecutable(method: Method, instance: Any): ExecutableAction<Any?> {
        return object : DiscordExecutableAction<Any?> {
            override fun execute(context: CommandContext, event: MessageReceivedEvent): Any? {
                return method.invoke(instance, context, event)
            }
        }
    }

    private fun Method.getPermissionLevel(): PermissionLevel? =
        if (this.isAnnotationPresent(PermissionLevel::class.java)) this.getAnnotation(PermissionLevel::class.java)
        else null
}
