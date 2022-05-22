package io.desolve.parser.parsers

import io.desolve.config.impl.EnvTableRepositoryConfig
import io.desolve.parser.ParsedProject
import io.desolve.parser.ProjectParser
import io.desolve.parser.ProjectType
import io.desolve.parser.compile.BuildResultType
import io.desolve.parser.compile.type.GradleBuildTask
import java.io.File
import java.io.FileReader
import java.util.concurrent.CompletableFuture

object GroovyGradleProjectParser : ProjectParser
{
    override fun parse(directory: File): CompletableFuture<ParsedProject?>
    {
        return parse(directory, null)
    }

    fun parse(directory: File, parent: ParsedProject?): CompletableFuture<ParsedProject?>
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
                val file = File(directory, "settings.gradle")

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
                println("${artifactId}:${groupId}:${version}")
                if (parent == null)
                {
                    println("null parent")
                    return@supplyAsync null
                }

                artifactId = artifactId ?: parent.artifactId
                version = version ?: parent.version
                groupId = groupId ?: parent.groupId
            }

            val buildResult = GradleBuildTask()
                .build(directory)
                .join()

            if (buildResult.file == null || buildResult.result == BuildResultType.Failed)
            {
                println("file is null")
                return@supplyAsync null
            }

            val project = ParsedProject(
                groupId!!, artifactId!!, version!!,
                buildResult.file, EnvTableRepositoryConfig.getDirectory(), parent
            )

            val subprojects = traverseRecursively(directory) {
                ProjectType.Gradle.matchesType(it)
            }.mapNotNull {
                parse(directory, project).join()
            }

            return@supplyAsync project.apply {
                this.children.addAll(subprojects)
            }
        }
    }

    private fun traverseRecursively(directory: File, filter: (File) -> Boolean): List<File>
    {
        val files = directory.listFiles()
        val list = mutableListOf<File>()

        if (!directory.isDirectory || files == null)
        {
            return list
        }

        for (file in files)
        {
            if (!filter(file))
            {
                continue
            }

            list.add(directory)
            list.addAll(traverseRecursively(directory, filter))
        }

        return list
    }

    private fun scanForProperty(line: String, id: String, action: (String) -> Unit)
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