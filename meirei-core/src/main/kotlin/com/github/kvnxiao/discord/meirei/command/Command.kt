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
package com.github.kvnxiao.discord.meirei.command

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Command(
    /**
     * Defines the prefix of the command. Defaults to [CommandDefaults.PREFIX].
     */
    val prefix: String = CommandDefaults.PREFIX,
    /**
     * Defines the unique name identifier of the command. This field is always required.
     */
    val uniqueName: String,
    /**
     * Defines the aliases for the command represented in an array of strings. This field is always required.
     */
    val aliases: Array<String>,
    /**
     * Defines the description of the command. Defaults to [CommandDefaults.NO_DESCRIPTION].
     */
    val description: String = CommandDefaults.NO_DESCRIPTION,
    /**
     * Defines the usage of the command. Defaults to [CommandDefaults.NO_USAGE].
     */
    val usage: String = CommandDefaults.NO_USAGE,
    /**
     * Defines the unique name of the parent command which implies that this command will be a subcommand of that parent.
     * This is used to link subcommands to their parent commands. Defaults to [CommandDefaults.PARENT_NAME] as an empty string representing no parent command.
     */
    val parentName: String = CommandDefaults.PARENT_NAME,
    /**
     * Defines whether the command should execute alongside its subcommand or be skipped when subcommands are processed.
     * Defaults to [CommandDefaults.EXEC_ALONGSIDE_SUBCOMMANDS].
     */
    val execWithSubcommands: Boolean = CommandDefaults.EXEC_ALONGSIDE_SUBCOMMANDS,
    /**
     * Defines whether the command is disabled. Defaults to [CommandDefaults.IS_DISABLED].
     */
    val isDisabled: Boolean = CommandDefaults.IS_DISABLED
)