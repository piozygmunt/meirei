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
package com.github.kvnxiao.discord.meirei.annotations.parser

import com.github.kvnxiao.discord.meirei.command.CommandPackage

data class CommandRelations(
    val pkg: CommandPackage
) {
    val subPkgs: MutableSet<CommandRelations> = mutableSetOf()

    fun toTreeString(depth: Int = 0): String {
        return pkg.commandProperties.id +
            if (subPkgs.isNotEmpty()) "\n${"\t".repeat(depth + 1)}${subPkgs.joinToString(separator = "\n${"\t".repeat(depth + 1)}") { it.toTreeString(depth + 1) }}"
            else ""
    }

}