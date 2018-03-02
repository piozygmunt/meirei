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
package com.github.kvnxiao.discord.meirei.command.parser

import com.github.kvnxiao.discord.meirei.command.annotations.Permissions
import com.github.kvnxiao.discord.meirei.command.annotations.RegistryAware
import com.github.kvnxiao.discord.meirei.command.permission.PermissionProperties
import com.github.kvnxiao.kommandant.command.parser.AnnotationParserImpl
import java.lang.reflect.Method

abstract class AnnotationParser : AnnotationParserImpl() {

    /**
     * Gets the [Permissions] annotation from a method declaration.
     */
    protected fun Method.getPermissions(): Permissions? =
        if (this.isAnnotationPresent(Permissions::class.java)) this.getAnnotation(Permissions::class.java)
        else null

    /**
     * Checks if the [RegistryAware] annotation is present for a method declaration.
     */
    protected fun Method.isRegistryAware(): Boolean =
        this.isAnnotationPresent(RegistryAware::class.java)

    protected open fun createPermissionProperties(method: Method, permissions: Permissions?): PermissionProperties {
        return if (permissions != null) {
            PermissionProperties(
                requireMention = permissions.reqMention,
                forceDmFromSender = permissions.forceDmReply,
                allowDmFromSender = permissions.allowDm,
                removeCallMsg = permissions.removeCallMsg,
                rateLimitPeriodInMs = permissions.rateLimitPeriodMs,
                rateLimitOnGuild = permissions.rateLimitOnGuild,
                tokensPerPeriod = permissions.tokensPerPeriod,
                reqBotOwner = permissions.reqBotOwner,
                reqGuildOwner = permissions.reqGuildOwner
            )
        } else {
            PermissionProperties()
        }
    }
}
