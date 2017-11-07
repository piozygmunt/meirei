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
package com.github.kvnxiao.discord.meirei.d4j

import sx.blah.discord.api.internal.json.objects.EmbedObject
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.util.RateLimitException
import sx.blah.discord.util.RequestBuffer

fun IChannel.sendBuffered(content: String): RequestBuffer.RequestFuture<Void> = RequestBuffer.request {
    try {
        this.sendMessage(content)
    } catch (e: RateLimitException) {
        throw e
    }
}

fun IChannel.sendBuffered(embed: EmbedObject): RequestBuffer.RequestFuture<Void> = RequestBuffer.request {
    try {
        this.sendMessage(embed)
    } catch (e: RateLimitException) {
        throw e
    }
}