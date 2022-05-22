package io.desolve.parser.parsers

import io.desolve.config.impl.EnvTableRepositoryConfig
import io.desolve.parser.ParsedProject
import io.desolve.parser.ProjectParser
import io.desolve.parser.compile.BuildResultType
import io.desolve.parser.compile.type.GradleBuildTask
import java.io.File
import java.io.FileReader
import java.util.concurrent.CompletableFuture

object GroovyGradleProjectParser : ProjectParser
{
    override fun parse(directory: File): CompletableFuture<ParsedProject?>
    {
        return CompletableFuture.supplyAsync {
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
                return@supplyAsync null
            }

            val buildResult = GradleBuildTask()
                .build(directory)
                .join()

            if (buildResult.file == null || buildResult.result == BuildResultType.Failed)
            {
                return@supplyAsync null
            }

            return@supplyAsync ParsedProject(
                groupId!!, artifactId!!, version!!, buildResult.file, EnvTableRepositoryConfig.getDirectory()
            )
        }
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
                    .replace("\"", "")
                    .replace(" ", "")
            )
        }
    }

    private fun scanForJar(directory: File, artifactId: String, version: String): File?
    {
        val files = directory.listFiles() ?: return null

        for (file in files)
        {
            println("${artifactId}-${version}")
            if (!file.name.startsWith("${artifactId}-${version}") || !file.name.endsWith(".jar"))
            {
                continue
            }

            return file
        }

        return null
    }
}

enum class GradlewArguments
{

}