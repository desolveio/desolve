package io.desolve.parser.parsers

import io.desolve.parser.ParsedProject
import io.desolve.parser.ProjectParser
import io.desolve.parser.ProjectType
import io.desolve.parser.compile.type.MavenBuildTask
import java.io.File
import java.io.FileReader
import java.util.concurrent.CompletableFuture

/**
 * @author Patrick Zondervan
 * @since 5/23/2022
 */
object MavenProjectParser : ProjectParser
{
    override val projectType = ProjectType.Maven

    override fun parse(directory: File): CompletableFuture<ParsedProject?>
    {
        return parse(directory, null);
    }

    override fun parse(directory: File, parent: ParsedProject?): CompletableFuture<ParsedProject?>
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

            if (artifactId == null || version == null || groupId == null)
            {
                if (parent == null)
                {
                    return@supplyAsync null
                }

                artifactId = artifactId ?: directory.name
                version = version ?: parent.version
                groupId = groupId ?: parent.groupId
            }

            val buildResult = buildProject(
                MavenBuildTask(), parent, directory
            ) {
                it.build(directory)
            }.join() ?: return@supplyAsync null

            return@supplyAsync parseFromResult(
                groupId!!,
                artifactId!!,
                version!!,
                buildResult,
                directory,
                parent
            ).join()
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
