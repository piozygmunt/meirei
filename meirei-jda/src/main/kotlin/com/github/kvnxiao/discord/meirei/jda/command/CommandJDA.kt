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

import com.github.kvnxiao.discord.meirei.command.DiscordCommandPackage
import com.github.kvnxiao.discord.meirei.command.permission.PermissionProperties
import com.github.kvnxiao.discord.meirei.jda.command.permission.PermissionLevelDefaults
import com.github.kvnxiao.kommandant.command.CommandProperties
import com.github.kvnxiao.kommandant.command.ExecutableAction
import com.github.kvnxiao.kommandant.command.ExecutionErrorHandler
import net.dv8tion.jda.core.Permission
import java.util.EnumSet

class CommandJDA(
    executable: ExecutableAction<Any?>,
    properties: CommandProperties,
    permissions: PermissionProperties,
    errorHandler: ExecutionErrorHandler = DefaultErrorHandler(),
    permissionLevel: EnumSet<Permission> = PermissionLevelDefaults.DEFAULT_PERMS_RW,
    isRegistryAware: Boolean = false
) : DiscordCommandPackage<Permission>(executable, properties, permissions, errorHandler, permissionLevel,
    isRegistryAware)
