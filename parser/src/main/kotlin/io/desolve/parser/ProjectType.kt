package io.desolve.parser

import java.io.File

/**
 * Every enum should have a string, which can be split using
 * the '|' pipe symbol.
 */
enum class ProjectType(val recognisableFiles: String)
{
    Maven("pom.xml"),
    Gradle("build.gradle"),
    GradleKotlin("build.gradle.kts");

    fun matchesType(directory: File): Boolean
    {
        for (listFile in directory.listFiles()!!)
        {
            val names = recognisableFiles.split("|")

            if (!names.contains(listFile.name))
            {
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