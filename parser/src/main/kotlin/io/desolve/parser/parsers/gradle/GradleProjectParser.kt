package io.desolve.parser.parsers.gradle

import io.desolve.parser.ParsedProject
import io.desolve.parser.ProjectParser
import io.desolve.parser.compile.type.GradleBuildTask
import java.io.File
import java.io.FileReader
import java.util.concurrent.CompletableFuture

abstract class GradleProjectParser : ProjectParser
{
    abstract val buildFileName: String
    abstract val settingsFileName: String

    override fun parse(directory: File): CompletableFuture<ParsedProject?>
    {
        return parse(directory, null)
    }

    override fun parse(directory: File, parent: ParsedProject?): CompletableFuture<ParsedProject?>
    {
        return CompletableFuture.supplyAsync {
            val gradleBuild = FileReader(File(directory, buildFileName))

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
                val file = File(directory, settingsFileName)

                if (file.exists())
                {
                    val gradleSettings = FileReader(file)

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

            val buildResult = buildProject(GradleBuildTask(), parent, directory) { it.build(directory) }
                .join() ?: return@supplyAsync null

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

    open fun scanForProperty(line: String, id: String, action: (String) -> Unit)
    {
        if (line.replace(" ", "").startsWith(id))
        {
            action(
                line
                    .replaceFirst(id, "")
                    .replaceFirst("=", "")
                    .replace("'", "")
                    .replace("\"", "")
                    .replace(" ", "")
            )
        }
    }
}
