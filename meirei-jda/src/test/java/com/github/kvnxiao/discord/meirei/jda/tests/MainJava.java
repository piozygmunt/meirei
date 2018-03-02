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
package com.github.kvnxiao.discord.meirei.jda.tests;

import com.github.kvnxiao.discord.meirei.jda.Meirei;
import com.github.kvnxiao.discord.meirei.jda.tests.annotated.AnnotatedCommand;
import com.github.kvnxiao.discord.meirei.jda.tests.annotated.PermissionCommand;
import com.github.kvnxiao.discord.meirei.jda.tests.annotated.ReadyListenerCommand;
import com.github.kvnxiao.discord.meirei.jda.tests.annotated.RegistryAwareCommand;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;

public class MainJava {

    public static void main(String[] args) {
        final String token = System.getenv("TEST_BOT_TOKEN");
        if (token == null) {
            throw new IllegalArgumentException("TEST_BOT_TOKEN environment variable is missing!");
        }

        final JDABuilder builder = new JDABuilder(AccountType.BOT)
                .setToken(token);

        // Add Meirei to discord client
        final Meirei meirei = new Meirei(builder);

//        // Builder-based command
//        meirei.addCommands(new CommandBuilder("test.builder")
//                .aliases("builder")
//                .build((context, event) -> event.getTextChannel().sendMessage("This command was created using a CommandBuilder class.").queue())
//        );

        // Build client and log in to discord
        meirei.addAnnotatedCommands(
                new AnnotatedCommand(),
                new PermissionCommand(),
                new RegistryAwareCommand(),
                new ReadyListenerCommand()
        );

        try {
            builder.buildAsync();
        } catch (Exception ignored) {

        }
    }

}
