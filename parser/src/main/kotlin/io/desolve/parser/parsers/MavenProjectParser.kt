package io.desolve.parser.parsers

import io.desolve.config.impl.EnvTableRepositoryConfig
import io.desolve.parser.ParsedProject
import io.desolve.parser.ProjectParser
import java.io.File
import java.io.FileReader
import java.util.concurrent.CompletableFuture

object MavenProjectParser : ProjectParser
{
    override fun parse(directory: File): CompletableFuture<ParsedProject?>
    {
        return CompletableFuture.supplyAsync {
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
                return@supplyAsync null
            }

            return@supplyAsync ParsedProject(groupId!!, artifactId!!, version!!, File(""), EnvTableRepositoryConfig.getDirectory())
        }
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