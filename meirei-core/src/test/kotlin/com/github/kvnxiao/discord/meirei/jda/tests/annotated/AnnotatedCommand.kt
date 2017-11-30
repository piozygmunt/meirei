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
package com.github.kvnxiao.discord.meirei.jda.tests.annotated

import com.github.kvnxiao.discord.meirei.annotations.Command
import com.github.kvnxiao.discord.meirei.annotations.CommandGroup
import com.github.kvnxiao.discord.meirei.annotations.RegistryAware

@CommandGroup("test.annotated.grouped")
class AnnotatedCommand {

    @Command(
        id = "parent",
        aliases = ["parent"],
        prefix = "/"
    )
    fun parent() = Unit

    @Command(
        id = "child",
        parentId = "parent",
        aliases = ["child"]
    )
    fun child() = Unit

    @Command(
        id = "third",
        parentId = "child",
        aliases = ["third"]
    )
    fun third() = Unit

    @Command(
        id = "fourth",
        parentId = "third",
        aliases = ["fourth"]
    )
    @RegistryAware
    fun fourth() = Unit

    @Command(
        id = "beta",
        parentId = "child",
        aliases = ["beta"]
    )
    fun beta() = Unit

    @Command(
        id = "charlie",
        parentId = "beta",
        aliases = ["charlie"]
    )
    @RegistryAware
    fun charlie() = Unit
}
