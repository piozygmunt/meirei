package com.github.kvnxiao.discord.meirei.jda.command

import com.github.kvnxiao.discord.meirei.command.Command
import java.lang.reflect.Method

interface ICommandParser {

    /**
     * Parse [CommandAnn] annotations in a class and returns a list of commands created from those annotations.
     *
     * @param[instance] The instance object for which its class is to be parsed.
     * @return[List] The list of commands created after parsing.
     */
    fun parseAnnotations(instance: Any): List<ICommand>

    /**
     * Creates a command by parsing a single [CommandAnn] annotation, with its execution method set as the method
     * targeted by the annotation.
     *
     * @param[instance] The instance object for which its class is to be parsed.
     * @param[method] The method to invoke for command execution.
     * @param[annotation] The annotation to parse.
     * @return[ICommand] A newly created command with properties taken from the annotation.
     */
    fun createCommand(instance: Any, method: Method, annotation: Command): ICommand

}