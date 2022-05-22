package io.github.devrawr.desolver.parser.parsers

import io.github.devrawr.desolver.config.impl.EnvTableRepositoryConfig
import io.github.devrawr.desolver.parser.ParsedProject
import io.github.devrawr.desolver.parser.ProjectParser
import java.io.File
import java.io.FileReader

object MavenProjectParser : ProjectParser
{
    override fun parse(directory: File): ParsedProject?
    {
        val fileReader = FileReader(File(directory, "pom.xml"))
        var groupId: String? = null
        var artifactId: String? = null
        var version: String? = null

        for (current in fileReader.readLines())
        {
            val line = current.replace(" ", "")

            if (groupId != null && artifactId != null && version != null)
            {
                break
            }

            scanForTag(
                line = line,
                tag = "groupId"
            ) {
                groupId = it
            }

            scanForTag(
                line = line,
                tag = "artifactId"
            ) {
                artifactId = it
            }

            scanForTag(
                line = line,
                tag = "version"
            ) {
                version = it
            }
        }

        if (groupId == null || artifactId == null || version == null)
        {
            return null
        }

        return ParsedProject(groupId!!, artifactId!!, version!!, File(""), EnvTableRepositoryConfig.getDirectory())
    }

    private fun scanForTag(line: String, tag: String, action: (String) -> Unit)
    {
        if (line.startsWith("<${tag}>") && line.endsWith("</${tag}>"))
        {
            action(
                line
                    .replace("<${tag}>", "")
                    .replace("</${tag}>", "")
            )
        }
    }
}