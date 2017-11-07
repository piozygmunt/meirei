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
package com.github.kvnxiao.discord.meirei.annotations

/**
 * Top-level class annotation to describe the "group" id of a class that may contain multiple annotated command
 * definitions. Each annotated command declared within the class will have their names prepended by this annotation's
 * value.
 *
 * For example, a class named FooCommands annotated with @CommandGroup("foo"), and containing a command named "bar".
 * The id set for the "bar" command within its [CommandProperties] will be "foo.bar".
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class CommandGroup(
    val id: String
)