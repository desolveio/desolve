package io.desolve.parser

import io.desolve.config.impl.EnvTableRepositoryConfig
import io.desolve.parser.compile.BuildResult
import io.desolve.parser.compile.BuildResultType
import io.desolve.parser.compile.BuildTask
import io.desolve.parser.compile.type.GradleBuildTask
import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * @author Patrick Zondervan
 * @since 5/23/2022
 */
interface ProjectParser
{
    val projectType: ProjectType

    fun parse(directory: File): CompletableFuture<ParsedProject?> =
        parse(directory, null)

    fun parse(directory: File, parent: ParsedProject?): CompletableFuture<ParsedProject?>

    fun buildProject(
        task: BuildTask,
        parent: ParsedProject?,
        directory: File,
        initialize: (BuildTask) -> CompletableFuture<BuildResult>
    ): CompletableFuture<BuildResult?>
    {
        return if (parent == null)
        {
            initialize(task).thenApply {
                if (it.file == null || it.result == BuildResultType.Failed)
                {
                    return@thenApply null
                }

                return@thenApply it
            }
        } else
        {
            CompletableFuture.supplyAsync {
                val file = task.scanForJar(
                    File(
                        directory,
                        "/build/libs/"
                    )
                )

                return@supplyAsync BuildResult(
                    when (file)
                    {
                        null -> BuildResultType.Failed
                        else -> BuildResultType.Success
                    },
                    file
                )
            }
        }
    }

    fun parseFromResult(
        groupId: String,
        artifactId: String,
        version: String,
        result: BuildResult,
        directory: File,
        parent: ParsedProject?
    ): CompletableFuture<ParsedProject>
    {
        return CompletableFuture.supplyAsync {
            val project = ParsedProject(
                groupId, artifactId, version,
                result.file,
                EnvTableRepositoryConfig.getBuildDirectory(),
                result, parent
            )

            val subprojects = traverseFiles(directory) {
                projectType.matchesType(it)
            }.mapNotNull { parse(it, project).join() }

            project.apply {
                this.children = mutableListOf(*subprojects.toTypedArray())
            }
        }
    }


    private fun traverseFiles(
        directory: File,
        filter: (File) -> Boolean
    ): List<File>
    {
        val files = directory.listFiles()

        if (!directory.isDirectory || files == null)
        {
            return emptyList()
        }

        return files
            .filter(filter)
            .toList()
    }
}