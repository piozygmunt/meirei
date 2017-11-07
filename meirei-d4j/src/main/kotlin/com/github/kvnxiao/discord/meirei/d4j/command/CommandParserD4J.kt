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
package com.github.kvnxiao.discord.meirei.d4j.command

import com.github.kvnxiao.discord.meirei.annotations.RegistryAware
import com.github.kvnxiao.discord.meirei.annotations.parser.AnnotationParser
import com.github.kvnxiao.discord.meirei.command.CommandContext
import com.github.kvnxiao.discord.meirei.command.DiscordCommand
import com.github.kvnxiao.discord.meirei.d4j.permission.LevelDefaults
import com.github.kvnxiao.discord.meirei.d4j.permission.PermissionLevel
import com.github.kvnxiao.discord.meirei.d4j.permission.PermissionPropertiesD4J
import com.github.kvnxiao.discord.meirei.permission.PermissionData
import com.github.kvnxiao.discord.meirei.permission.PermissionProperties
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.Permissions
import java.lang.reflect.Method
import java.util.EnumSet

class CommandParserD4J : AnnotationParser() {

    override fun createCommand(id: String, isRegistryAware: Boolean, method: Method, instance: Any): DiscordCommand {
        return object : CommandD4J(id, method.isAnnotationPresent(RegistryAware::class.java)) {
            override fun execute(context: CommandContext, event: MessageReceivedEvent) {
                method.invoke(instance, context, event)
            }
        }
    }

    private fun Method.getPermissionLevel(): PermissionLevel? = if (this.isAnnotationPresent(PermissionLevel::class.java)) this.getAnnotation(PermissionLevel::class.java) else null
    private fun PermissionLevel?.createPermissionLevels(): EnumSet<Permissions>? {
        if (this == null) return null
        val levels: EnumSet<Permissions> = java.util.EnumSet.noneOf(Permissions::class.java)
        levels.addAll(this.level)
        return levels
    }

    override fun Method.createPermissionProperties(): PermissionProperties {
        val permissions = this.getPermissions()
        val permissionLevel = this.getPermissionLevel()

        val level: EnumSet<Permissions> = permissionLevel.createPermissionLevels() ?: LevelDefaults.DEFAULT_PERMS_RW

        return if (permissions != null) {
            PermissionPropertiesD4J(PermissionData(
                requireMention = permissions.reqMention,
                forceDmFromSender = permissions.forceDm,
                forceDmReply = permissions.forceDmReply,
                allowDmFromSender = permissions.allowDm,
                removeCallMsg = permissions.removeCallMsg,
                rateLimitPeriodInMs = permissions.rateLimitPeriodMs,
                rateLimitOnGuild = permissions.rateLimitOnGuild,
                tokensPerPeriod = permissions.tokensPerPeriod,
                reqBotOwner = permissions.reqBotOwner,
                reqGuildOwner = permissions.reqGuildOwner
            ), level)
        } else {
            PermissionPropertiesD4J()
        }
    }
}
