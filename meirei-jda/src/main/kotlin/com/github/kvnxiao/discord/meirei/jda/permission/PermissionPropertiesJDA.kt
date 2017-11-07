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
package com.github.kvnxiao.discord.meirei.jda.permission

import com.github.kvnxiao.discord.meirei.permission.PermissionData
import com.github.kvnxiao.discord.meirei.permission.PermissionProperties
import net.dv8tion.jda.core.Permission
import java.util.EnumSet

data class PermissionPropertiesJDA(
    override val data: PermissionData = PermissionData(),
    val level: EnumSet<Permission> = LevelDefaults.DEFAULT_PERMS_RW
) : PermissionProperties()