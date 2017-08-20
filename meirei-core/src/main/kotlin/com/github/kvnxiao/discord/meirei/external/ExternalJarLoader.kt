package com.github.kvnxiao.discord.meirei.external

import com.github.kvnxiao.discord.meirei.Meirei
import java.io.File
import java.io.IOException
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Paths
import java.util.jar.JarFile
import kotlin.streams.toList

object ExternalJarLoader {

    /**
     * Jar files end with extension ".jar"
     */
    const val JAR_EXTENSION = ".jar"

    /**
     * Loads jars from a folder and returns a map of -> jar file name : list of class names.
     *
     * @param[folder] The folder to read from.
     * @return[Map] A map of .jar file names to lists of class names corresponding to those jars.
     */
    fun loadJars(folder: String): Map<String, List<String>> {
        try {
            val paths = Files.list(Paths.get(folder))
                .filter { it.toString().toLowerCase().endsWith(JAR_EXTENSION) }
                .toList()
            val mutableMap: MutableMap<String, List<String>> = mutableMapOf()
            paths.forEach {
                try {
                    mutableMap[it.fileName.toString()] = loadJarFile(it.toFile())
                } catch (e: IOException) {
                    Meirei.LOGGER.error("Could not load '${it.fileName}' as a .jar file!")
                }
            }
            return mutableMap.toMap()
        } catch (e: IOException) {
            Meirei.LOGGER.error("Could not get list of .jar files when attempting to load external commands!")
        }
        return emptyMap()
    }

    /**
     * Loads a single jar file for a list of classes.
     *
     * @param[file] The .jar file to load.
     * @return[List] The list of class names as strings.
     */
    @Throws(IOException::class)
    fun loadJarFile(file: File): List<String> {
        if (file.isFile && file.name.endsWith(JAR_EXTENSION)) {
            JarFile(file).use {
                // Get system class loader, and file as a url
                val classLoader = ClassLoader.getSystemClassLoader() as URLClassLoader
                val url = file.toURI().toURL()

                // This jar was already loaded, so we may skip it
                if (classLoader.urLs.any { it == url }) return emptyList()

                // Access the addURL method
                val addUrlMethod = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
                addUrlMethod.isAccessible = true
                addUrlMethod.invoke(classLoader, url)

                // Get a list of valid classes from the .jar file
                return it.stream()
                    .filter { !it.isDirectory && it.name.toLowerCase().endsWith(".class") }
                    .map { it.name.replace('/', '.').substring(0, it.name.length - ".class".length) }
                    .toList()
            }
        } else {
            Meirei.LOGGER.error("$file could not be loaded as an external jar dependency.")
        }
        return emptyList()
    }

}