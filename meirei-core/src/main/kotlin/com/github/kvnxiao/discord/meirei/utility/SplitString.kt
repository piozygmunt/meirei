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
package com.github.kvnxiao.discord.meirei.utility

data class SplitString(val first: String, val second: String?, val delimiter: String = "") {
    companion object {
        private const val R: Char = '\r'
        private const val N: Char = '\n'
        private const val SPACE: Char = ' '
        @JvmStatic
        private val LINE_SEPARATOR: String = System.lineSeparator()

        @JvmStatic
        fun splitString(content: String): SplitString {
            var indexSpace = -1
            var indexNewLine = -1
            var nextIndex = -1
            val max = content.length
            for (i in 0 until max) {
                when (content[i]) {
                    R -> {
                        indexNewLine = i
                        nextIndex = i + 2
                    }
                    N -> {
                        indexNewLine = i
                        nextIndex = i + 1
                    }
                    SPACE -> {
                        indexSpace = i
                        nextIndex = i + 1
                    }
                }
                if (indexSpace > -1) {
                    return SplitString(content.substring(0, indexSpace), content.substring(nextIndex, max), SPACE.toString())
                } else if (indexNewLine > -1) {
                    return SplitString(content.substring(0, indexNewLine), content.substring(nextIndex, max), LINE_SEPARATOR)
                }
            }
            return SplitString(content, null)
        }
    }
}
