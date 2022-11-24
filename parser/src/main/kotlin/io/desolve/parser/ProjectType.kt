package io.desolve.parser

import java.io.File

/**
 * @author Patrick Zondervan
 * @since 5/23/2022
 */
enum class ProjectType(
    val fileRegex: Regex? = null,
    val fileNames: Array<String>? = null
)
{
    // TODO: 24/05/2022 fix thoses regexes, currently:
    // - GradleKotlin | does not work
    // - Gradle       | seems to work
    // - Maven        | untested
    Maven(
        fileNames = arrayOf("pom.xml")
    ),
    Gradle(
        fileNames = arrayOf(
            "build.gradle",
            "settings.gradle",
            "build.gradle.kts",
            "settings.gradle.kts"
        )
    );

    fun matchesType(directory: File): Boolean
    {
        val files = directory.listFiles()

        if (files == null || !directory.isDirectory)
        {
            return false
        }

        for (listFile in files)
        {
            if (!((fileRegex != null && listFile.name.matches(fileRegex))
                        || (fileNames != null && fileNames.contains(listFile.name)))
            )
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
