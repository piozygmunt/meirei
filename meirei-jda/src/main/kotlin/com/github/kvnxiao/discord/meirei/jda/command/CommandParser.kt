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
package com.github.kvnxiao.discord.meirei.jda.command

import com.github.kvnxiao.discord.meirei.annotations.Command
import com.github.kvnxiao.discord.meirei.annotations.CommandGroup
import com.github.kvnxiao.discord.meirei.annotations.Permissions
import com.github.kvnxiao.discord.meirei.annotations.RegistryAware
import com.github.kvnxiao.discord.meirei.annotations.parser.AnnotationParser
import com.github.kvnxiao.discord.meirei.command.CommandContext
import com.github.kvnxiao.discord.meirei.command.CommandPackage
import com.github.kvnxiao.discord.meirei.command.CommandProperties
import com.github.kvnxiao.discord.meirei.jda.permission.LevelDefaults
import com.github.kvnxiao.discord.meirei.jda.permission.PermissionLevel
import com.github.kvnxiao.discord.meirei.jda.permission.PermissionPropertiesJDA
import com.github.kvnxiao.discord.meirei.permission.PermissionData
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import java.lang.reflect.Method
import java.util.EnumSet

class CommandParser : AnnotationParser {

    override fun createCommandPackage(instance: Any, method: Method, annotation: Command): CommandPackage {
        val permissionAnnotation: Permissions? = if (method.isAnnotationPresent(Permissions::class.java)) method.getAnnotation(Permissions::class.java) else null
        val permissionLevelAnn: PermissionLevel? = if (method.isAnnotationPresent(PermissionLevel::class.java)) method.getAnnotation(PermissionLevel::class.java) else null
        val commandGroup: CommandGroup? = if (instance.javaClass.isAnnotationPresent(CommandGroup::class.java)) instance.javaClass.getAnnotation(CommandGroup::class.java) else null

        val id = if (commandGroup != null) "${commandGroup.id}.${annotation.id}" else annotation.id
        val parentId = if (commandGroup != null) "${commandGroup.id}.${annotation.parentId}" else annotation.parentId
        val properties = CommandProperties(
            prefix = annotation.prefix,
            id = id,
            parentId = parentId,
            description = annotation.description,
            usage = annotation.usage,
            execWithSubCommands = annotation.execWithSubcommands,
            isDisabled = annotation.isDisabled,
            aliases = annotation.aliases.toSet()
        )

        val level: EnumSet<Permission> = createPermissionLevels(permissionLevelAnn) ?: LevelDefaults.DEFAULT_PERMS_RW
        val permissionProperties: PermissionPropertiesJDA = createPermissions(permissionAnnotation, level) ?: PermissionPropertiesJDA(level = level)

        val command = object : CommandJDA(id, method.isAnnotationPresent(RegistryAware::class.java)) {
            override fun execute(context: CommandContext, event: MessageReceivedEvent) {
                method.invoke(instance, context, event)
            }
        }

        return CommandPackage(command, properties, permissionProperties)
    }

    private fun createPermissionLevels(permissionLevelAnn: PermissionLevel?): EnumSet<Permission>? {
        if (permissionLevelAnn == null) return null
        val levels: EnumSet<Permission> = EnumSet.noneOf(Permission::class.java)
        levels.addAll(permissionLevelAnn.level)
        return levels
    }

    private fun createPermissions(permissionAnn: Permissions?, level: EnumSet<Permission>): PermissionPropertiesJDA? {
        if (permissionAnn == null) return null
        return PermissionPropertiesJDA(
            data = PermissionData(
                requireMention = permissionAnn.reqMention,
                forceDmFromSender = permissionAnn.forceDmReply,
                allowDmFromSender = permissionAnn.allowDm,
                removeCallMsg = permissionAnn.removeCallMsg,
                rateLimitPeriodInMs = permissionAnn.rateLimitPeriodMs,
                rateLimitOnGuild = permissionAnn.rateLimitOnGuild,
                tokensPerPeriod = permissionAnn.tokensPerPeriod,
                reqBotOwner = permissionAnn.reqBotOwner,
                reqGuildOwner = permissionAnn.reqGuildOwner
            ),
            level = level
        )
    }

}