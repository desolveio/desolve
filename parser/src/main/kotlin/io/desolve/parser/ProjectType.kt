package io.github.devrawr.desolver.parser

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

    companion object
    {
        fun recognize(directory: File): ProjectType?
        {
            for (listFile in directory.listFiles()!!)
            {
                for (value in values())
                {
                    val names = value.recognisableFiles.split("|")

                    if (!names.contains(listFile.name))
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