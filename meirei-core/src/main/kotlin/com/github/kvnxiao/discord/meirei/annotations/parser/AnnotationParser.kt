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
package com.github.kvnxiao.discord.meirei.annotations.parser

import com.github.kvnxiao.discord.meirei.annotations.Command
import com.github.kvnxiao.discord.meirei.annotations.CommandGroup
import com.github.kvnxiao.discord.meirei.annotations.Permissions
import com.github.kvnxiao.discord.meirei.annotations.RegistryAware
import com.github.kvnxiao.discord.meirei.command.CommandDefaults
import com.github.kvnxiao.discord.meirei.command.CommandPackage
import com.github.kvnxiao.discord.meirei.command.CommandProperties
import com.github.kvnxiao.discord.meirei.command.DiscordCommand
import com.github.kvnxiao.discord.meirei.permission.PermissionData
import com.github.kvnxiao.discord.meirei.permission.PermissionProperties
import com.github.kvnxiao.discord.meirei.utility.CommandId
import java.lang.reflect.Method

abstract class AnnotationParser {

    /**
     * Parse [Command] annotations in a class and returns a list of commands created from those annotations.
     *
     * @param[instance] The instance object for which its class is to be parsed.
     * @return[List] The list of commands created after parsing.
     */
    open fun parseAnnotations(instance: Any): Set<CommandRelations> {
        val clazz = instance::class.java

        val subCommands: MutableSet<CommandRelations> = mutableSetOf()
        val commands: MutableMap<CommandId, CommandRelations> = mutableMapOf()
        val mainCommands: MutableSet<CommandRelations> = mutableSetOf()

        // Create and add command to bank for each executable method
        clazz.methods.forEach { method ->
            if (method.isAnnotationPresent(Command::class.java)) {
                val annotation: Command = method.getAnnotation(Command::class.java)

                // Create command to add to bank
                val commandPackage: CommandPackage = this.createCommandPackage(instance, method, annotation)
                val commandRelations = CommandRelations(commandPackage)
                // Add sub-commands and main commands to bank
                if (annotation.parentId != CommandDefaults.PARENT_ID) {
                    subCommands.add(commandRelations)
                } else {
                    mainCommands.add(commandRelations)
                }
                commands.put(commandPackage.commandProperties.id, commandRelations)
            }
        }

        // Link sub commands to parent
        subCommands.forEach { relation ->
            val subId = relation.pkg.commandProperties.id
            val parentId = relation.pkg.commandProperties.parentId
            commands[parentId]!!.subPkgs.add(commands[subId]!!)
        }

        subCommands.clear()
        commands.clear()

        return mainCommands
    }

    /**
     * Creates a command by parsing a single [Command] annotation, with its execution method set as the method
     * targeted by the annotation.
     *
     * @param[instance] The instance object for which its class is to be parsed.
     * @param[method] The method to invoke for command execution.
     * @param[annotation] The annotation to parse.
     * @return[DiscordCommand] A newly created command with properties taken from the annotation.
     */
    fun createCommandPackage(instance: Any, method: Method, annotation: Command): CommandPackage {
        val commandGroup = instance.javaClass.getCommandGroup()

        val id = appendGroup(annotation.id, commandGroup)
        val parentId = if (annotation.parentId != CommandDefaults.PARENT_ID) appendGroup(annotation.parentId, commandGroup) else annotation.parentId

        val properties = annotation.createProperties(id, parentId)
        val permissionProperties = method.createPermissionProperties()

        val command = createCommand(id, method.isAnnotationPresent(RegistryAware::class.java), method, instance)

        return CommandPackage(command, properties, permissionProperties)
    }

    abstract fun createCommand(id: String, isRegistryAware: Boolean, method: Method, instance: Any): DiscordCommand

    protected fun Method.getPermissions(): Permissions? = if (this.isAnnotationPresent(Permissions::class.java)) this.getAnnotation(Permissions::class.java) else null
    protected fun Class<Any>.getCommandGroup(): CommandGroup? = if (this.isAnnotationPresent(CommandGroup::class.java)) this.getAnnotation(CommandGroup::class.java) else null
    protected fun appendGroup(id: String, commandGroup: CommandGroup?) = if (commandGroup != null) "${commandGroup.id}.$id" else id

    protected open fun Command.createProperties(newId: String, newParentId: String): CommandProperties {
        return CommandProperties(
            prefix = this.prefix,
            id = newId,
            parentId = newParentId,
            description = this.description,
            usage = this.usage,
            execWithSubCommands = this.execWithSubcommands,
            isDisabled = this.isDisabled,
            aliases = this.aliases.toSet()
        )
    }

    protected open fun Method.createPermissionProperties(): PermissionProperties {
        val permissions = this.getPermissions()
        return if (permissions != null) {
            PermissionProperties(PermissionData(
                requireMention = permissions.reqMention,
                forceDmFromSender = permissions.forceDmReply,
                allowDmFromSender = permissions.allowDm,
                removeCallMsg = permissions.removeCallMsg,
                rateLimitPeriodInMs = permissions.rateLimitPeriodMs,
                rateLimitOnGuild = permissions.rateLimitOnGuild,
                tokensPerPeriod = permissions.tokensPerPeriod,
                reqBotOwner = permissions.reqBotOwner,
                reqGuildOwner = permissions.reqGuildOwner
            ))
        } else {
            PermissionProperties()
        }
    }
}
