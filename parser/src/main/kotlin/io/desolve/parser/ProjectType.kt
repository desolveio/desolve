package io.desolve.parser

import java.io.File

/**
 * @author Patrick Zondervan
 * @since 5/23/2022
 */
enum class ProjectType(val fileRegex: Regex)
{
    // TODO: 24/05/2022 fix thoses regexes, currently:
    // - GradleKotlin | does not work
    // - Gradle       | seems to work
    // - Maven        | untested
    Maven("pom.xml\$".toRegex()),
    Gradle("\\.gradle\$".toRegex()),
    GradleKotlin("\\.gradle.kts\$".toRegex());

    fun matchesType(directory: File): Boolean
    {
        val files = directory.listFiles()

        if (files == null || !directory.isDirectory)
        {
            return false
        }

        for (listFile in files)
        {
            if (!listFile.name.matches(fileRegex))
            {
                println(listFile.name)
                continue
            }

            return true
        }

        return false
    }

    companion object
    {
        fun recognize(directory: File): ProjectType?
        {
            for (listFile in directory.listFiles()!!)
            {
                for (value in values())
                {
                    if (!value.matchesType(directory))
                    {
                        continue
                    }

                    return value
                }
            }

            return null
        }
    }
}