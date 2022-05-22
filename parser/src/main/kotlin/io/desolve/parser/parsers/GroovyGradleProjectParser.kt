package io.desolve.parser.parsers

import io.desolve.config.impl.EnvTableRepositoryConfig
import io.desolve.parser.ParsedProject
import io.desolve.parser.ProjectParser
import java.io.File
import java.io.FileReader

object GroovyGradleProjectParser : ProjectParser
{
    override fun parse(directory: File): ParsedProject?
    {
        val gradleBuild = FileReader(File(directory, "build.gradle"))

        var groupId: String? = null
        var version: String? = null
        var artifactId: String? = null

        for (line in gradleBuild.readLines())
        {
            scanForProperty(
                line = line,
                id = "group"
            ) {
                groupId = it
            }

            scanForProperty(
                line = line,
                id = "version"
            ) {
                version = it
            }

            scanForProperty(
                line = line,
                id = "name"
            ) {
                artifactId = it
            }
        }


        // we could do this for every property, just lazy.
        if (artifactId == null)
        {
            val gradleSettings = FileReader(File(directory, "settings.gradle"))

            for (line in gradleSettings.readLines())
            {
                scanForProperty(
                    line = line,
                    id = "rootProject.name"
                ) {
                    artifactId = it
                }
            }
        }

        if (artifactId == null || version == null || groupId == null)
        {
            return null
        }

        var buildFile: File? = null
        val buildDirectory = File(directory, "build/libs")

        for (file in buildDirectory.listFiles()!!)
        {
            if (!file.name.startsWith("${artifactId}-${version}") || !file.name.endsWith(".jar"))
            {
                continue
            }

            buildFile = file
            break
        }

        if (buildFile == null)
        {
            return null
        }

        return ParsedProject(groupId!!, artifactId!!, version!!, buildFile, EnvTableRepositoryConfig.getDirectory())
    }

    private fun scanForProperty(line: String, id: String, action: (String) -> Unit)
    {
        if (line.startsWith(id))
        {
            action(
                line
                    .replaceFirst(id, "")
                    .replaceFirst("=", "")
                    .replace("'", "")
                    .replace(" ", "")
            )
        }
    }
}