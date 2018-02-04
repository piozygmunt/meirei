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
package com.github.kvnxiao.discord.meirei.utility;

/**
 * Java class for performance reasons
 */
public class StringSplitter {

    /**
     * Java string splitting utility method.
     *
     * @param str The string to split.
     * @param regex The regex pattern in string form.
     * @param limit The maximum number of substrings to split into.
     * @return A string array of the original string split by the provided regex and limit.
     */
    public static String[] split(String str, String regex, int limit) {
        return str.split(regex, limit);
    }

    /**
     * Java string splitting utility method with no limit specified.
     *
     * @param str The string to split.
     * @param regex The regex pattern in string form.
     * @return A string array of the original string split by the provided regex.
     */
    public static String[] split(String str, String regex) {
        return str.split(regex);
    }

    /** Single space character in String form: " ". */
    public static final String SPACE_LITERAL = " ";

    /** Single space character: ' '. */
    public static final char SPACE_CHAR = ' ';
}
