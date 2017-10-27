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
package com.github.kvnxiao.discord.meirei

import com.github.kvnxiao.discord.meirei.command.CommandPackage
import com.github.kvnxiao.discord.meirei.command.database.CommandRegistry
import com.github.kvnxiao.discord.meirei.command.database.CommandRegistryImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory

open class Meirei(
    val registry: CommandRegistry = CommandRegistryImpl()
) {
    companion object {
        @JvmStatic
        val LOGGER: Logger = LoggerFactory.getLogger(Meirei::class.java)

        const val DEFAULT_JAR_ENV_NAME = "jarfolder"
        const val DEFAULT_THREAD_ENV_NAME = "nthreads"
    }

    fun addCommands(vararg commandPackages: CommandPackage) {
        commandPackages.forEach {
            registry.addCommand(it.command, it.commandProperties, it.permissionProperties)
        }
    }

    fun addSubCommands(parentId: String, vararg commandPackage: CommandPackage) {
        commandPackage.forEach {
            registry.addSubCommand(it.command, it.commandProperties, it.permissionProperties, parentId)
        }
    }

}
