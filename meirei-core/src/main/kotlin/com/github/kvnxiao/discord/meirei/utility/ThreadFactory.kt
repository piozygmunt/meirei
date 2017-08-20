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
package com.github.kvnxiao.discord.meirei.utility

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * A simple thread factory with its name explicitly stated. This is used so that logged messages can differentiate between KommanDB processing threads and other threads.
 */
class ThreadFactory : ThreadFactory {
    private val group: ThreadGroup
    private val threadNumber = AtomicInteger(1)
    private val namePrefix: String

    init {
        val s = System.getSecurityManager()
        group = if (s != null)
            s.threadGroup
        else
            Thread.currentThread().threadGroup
        namePrefix = "MeireiExec pool-${poolNumber.getAndIncrement()}-thread-"
    }

    override fun newThread(r: Runnable): Thread {
        val t = Thread(group, r,
            namePrefix + threadNumber.getAndIncrement(),
            0)
        if (t.isDaemon)
            t.isDaemon = false
        if (t.priority != Thread.NORM_PRIORITY)
            t.priority = Thread.NORM_PRIORITY
        return t
    }

    companion object {
        private val poolNumber = AtomicInteger(1)
    }
}