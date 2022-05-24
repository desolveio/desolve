package io.desolve.parser.parsers

import io.desolve.config.impl.EnvTableRepositoryConfig
import io.desolve.parser.ParsedProject
import io.desolve.parser.ProjectParser
import io.desolve.parser.compile.BuildResult
import io.desolve.parser.compile.BuildResultType
import java.io.File
import java.io.FileReader
import java.util.concurrent.CompletableFuture

/**
 * @author Patrick Zondervan
 * @since 5/23/2022
 */
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

            // TODO: 5/22/2022 maven build task
            return@supplyAsync ParsedProject(groupId!!, artifactId!!, version!!, File(""), EnvTableRepositoryConfig.getDirectory(), BuildResult(BuildResultType.Success, null))
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
