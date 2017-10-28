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

import com.github.kvnxiao.discord.meirei.annotations.Command
import com.github.kvnxiao.discord.meirei.annotations.CommandGroup
import com.github.kvnxiao.discord.meirei.annotations.Permissions
import com.github.kvnxiao.discord.meirei.annotations.RegistryAware
import com.github.kvnxiao.discord.meirei.annotations.parser.AnnotationParser
import com.github.kvnxiao.discord.meirei.command.CommandPackage
import com.github.kvnxiao.discord.meirei.command.CommandProperties
import com.github.kvnxiao.discord.meirei.permission.PermissionData
import com.github.kvnxiao.discord.meirei.permission.PermissionProperties
import java.lang.reflect.Method

class CommandParserImpl : AnnotationParser {

    override fun createCommandPackage(instance: Any, method: Method, annotation: Command): CommandPackage {
        val permissionAnnotation: Permissions? = if (method.isAnnotationPresent(Permissions::class.java)) method.getAnnotation(Permissions::class.java) else null
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

        val permissionProperties: PermissionProperties = if (permissionAnnotation != null) {
            PermissionProperties(PermissionData(
                requireMention = permissionAnnotation.reqMention,
                forceDmFromSender = permissionAnnotation.forceDmReply,
                allowDmFromSender = permissionAnnotation.allowDm,
                removeCallMsg = permissionAnnotation.removeCallMsg,
                rateLimitPeriodInMs = permissionAnnotation.rateLimitPeriodMs,
                rateLimitOnGuild = permissionAnnotation.rateLimitOnGuild,
                tokensPerPeriod = permissionAnnotation.tokensPerPeriod,
                reqBotOwner = permissionAnnotation.reqBotOwner,
                reqGuildOwner = permissionAnnotation.reqGuildOwner
            ))
        } else {
            PermissionProperties()
        }

        val command = CommandImpl(id, method.isAnnotationPresent(RegistryAware::class.java))

        return CommandPackage(command, properties, permissionProperties)
    }
}