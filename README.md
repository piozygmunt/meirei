<h1>Meirei &nbsp;<sup><sup><sub>命令 [めいれい] (noun): command, instruction, order</sub></sup></sup></h1>

[![CircleCI](https://circleci.com/gh/kvnxiao/meirei/tree/master.svg?style=shield)](https://circleci.com/gh/kvnxiao/meirei/tree/master)
[![Release](https://jitpack.io/v/kvnxiao/meirei.svg)](https://jitpack.io/#kvnxiao/meirei)

A simple and extensible command framework for JVM-based Discord API wrappers,
supporting both [JDA](https://github.com/DV8FromTheWorld/JDA) and [Discord4J](https://github.com/austinv11/Discord4J).

_Documentation TBD_

## Getting It

Current release version: **0.8.0**. Requires Java 8 or higher to run. Replace `@VERSION@` with the release tag you want.

### Gradle

**build.gradle**
```gradle
  repositories {
    // ...
    maven { url 'https://jitpack.io' }
  }
  
  dependencies {
    // CHOOSE ONE OF
    compile "com.github.kvnxiao.meirei:meirei-jda:@VERSION@" // For JDA
    compile "com.github.kvnxiao.meirei:meirei-d4j:@VERSION@" // For Discord4J (D4J)
  }
```

### Maven

**pom.xml**

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.kvnxiao.meirei</groupId>
    <!--CHOOSE ONE OF-->
    <artifactId>meirei-jda</artifactId>
    <artifactId>meirei-d4j</artifactId>
    <version>@VERSION@</version>
</dependency>
```

## Features

- Easy command creation using **annotations**, constructors, and **builders**
- **Sub-commands** as priority "arguments" of parent commands
- **Permissions** for each command based on the Discord permission system
- **Rate-limit** each command on a per _user_ or per _guild_ basis with customizable cooldowns
- **Command context** for each command execution, including the ability to expose the command registry for reading (you can create your help commands this way!)
- An open command registry interface so that **anyone can implement their own command store** (want to use SQL or NoSQL? go right ahead!)
  - Comes with a basic command registry implementation already
- Use it in Kotlin _or_ Java (or any other JVM language), it's up to you!

## Getting Started

See the [wiki](https://github.com/kvnxiao/meirei/wiki) for examples and more info.

## Roadmap

TBD