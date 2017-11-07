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
package com.github.kvnxiao.discord.meirei.d4j.tests;

import com.github.kvnxiao.discord.meirei.Meirei;
import com.github.kvnxiao.discord.meirei.d4j.MeireiD4J;
import com.github.kvnxiao.discord.meirei.d4j.command.CommandBuilder;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

import static com.github.kvnxiao.discord.meirei.d4j.D4JUtilsKt.sendBuffered;

public class MainJava {

    public static void main(String[] args) {
        final String token = System.getenv("TEST_BOT_TOKEN");
        if (token == null) {
            throw new IllegalArgumentException("TEST_BOT_TOKEN environment variable is missing!");
        }

        final IDiscordClient client = new ClientBuilder()
                .withToken(token)
                .build();

        // Add Meirei to discord client
        final Meirei meirei = new MeireiD4J(client);

        // Builder-based command
        meirei.addCommands(new CommandBuilder("test.builder")
                .aliases("builder")
                .build((context, event) -> {
                    sendBuffered(event.getChannel(), "This command was created using a CommandBuilder class.");
                })
        );

        // Log in to discord
        client.login();
    }

}
