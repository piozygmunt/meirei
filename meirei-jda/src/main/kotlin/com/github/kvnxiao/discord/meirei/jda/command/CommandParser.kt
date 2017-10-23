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
import com.github.kvnxiao.discord.meirei.command.CommandDefaults
import com.github.kvnxiao.discord.meirei.command.CommandProperties
import com.github.kvnxiao.discord.meirei.jda.permission.LevelDefaults
import com.github.kvnxiao.discord.meirei.jda.permission.PermissionLevel
import com.github.kvnxiao.discord.meirei.jda.permission.PermissionPropertiesJDA
import com.github.kvnxiao.discord.meirei.permission.PermissionData
import com.github.kvnxiao.discord.meirei.annotations.Permissions
import com.github.kvnxiao.discord.meirei.command.CommandParser
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.EnumSet
import java.util.Stack

class CommandParser : CommandParser {

    override fun parseAnnotations(instance: Any): List<ICommand> {
        // Instantiate new instance of class to reference method invocations
        val clazz = instance::class.java

        // Use hashmaps to link sub commands to parent commands
        val subCommands: MutableMap<ICommand, String> = mutableMapOf()
        val commands: MutableMap<String, ICommand> = mutableMapOf()
        val mainCommands: Stack<ICommand> = Stack()

        // Create and add command to bank for each methods with 'executable' signature type
        for (method in clazz.methods) {
            if (method.isAnnotationPresent(Command::class.java)) {
                val annotation: Command = method.getAnnotation(Command::class.java)

                // Create command to add to bank
                val command: ICommand = this.createCommand(instance, method, annotation)

                // Add main commands and chainable main commands to command bank
                if (annotation.parentName != CommandDefaults.PARENT_NAME || annotation.parentName == annotation.name) {
                    subCommands.put(command, annotation.parentName)
                }
                if (annotation.parentName == CommandDefaults.PARENT_NAME || annotation.parentName == annotation.name) {
                    mainCommands.push(command)
                }
                commands.put(annotation.name, command)
            }
        }

        // Link sub commands to parent
        for ((subCommand, parentName) in subCommands) {
            commands[parentName]?.addSubCommand(subCommand)
        }

        // Clear utility containers as we are done adding all commands
        subCommands.clear()
        commands.clear()

        return mainCommands.toList()
    }

    override fun createCommand(instance: Any, method: Method, annotation: Command): ICommand {
        val permissionAnn: Permissions? = if (method.isAnnotationPresent(Permissions::class.java)) method.getAnnotation(Permissions::class.java) else null
        val permissionLevelAnn: PermissionLevel? = if (method.isAnnotationPresent(PermissionLevel::class.java)) method.getAnnotation(PermissionLevel::class.java) else null

        val properties = CommandProperties(
            prefix = annotation.prefix,
            name = annotation.name,
            description = annotation.description,
            usage = annotation.usage,
            execWithSubCommands = annotation.execWithSubcommands,
            isDisabled = annotation.isDisabled,
            aliases = annotation.aliases.toSet()
        )

        val level: EnumSet<Permission> = createPermissionLevels(permissionLevelAnn) ?: LevelDefaults.DEFAULT_PERMS_RW
        val permissionProperties: PermissionPropertiesJDA = createPermissions(permissionAnn, level) ?: PermissionPropertiesJDA(level = level)

        return object : ICommand(properties, permissionProperties) {
            @Throws(InvocationTargetException::class, IllegalAccessException::class)
            override fun executeWith(context: CommandContext, event: MessageReceivedEvent) {
                method.invoke(instance, context, event)
            }
        }
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
            props = PermissionData(
                requireMention = permissionAnn.reqMention,
                forceDmReply = permissionAnn.forceDmReply,
                allowDm = permissionAnn.allowDm,
                removeCallMsg = permissionAnn.removeCallMsg,
                rateLimitPeriodInMs = permissionAnn.rateLimitPeriodMs,
                rateLimitOnGuild = permissionAnn.rateLimitOnGuild,
                tokensPerPeriod = permissionAnn.tokensPerPeriod,
                reqBotOwner = permissionAnn.reqBotOwner,
                reqGuildOwner =permissionAnn.reqGuildOwner
            ),
            level = level
        )
    }

}