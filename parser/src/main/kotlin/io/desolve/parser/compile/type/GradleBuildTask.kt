package io.desolve.parser.compile.type

import io.desolve.parser.compile.BuildResult
import io.desolve.parser.compile.BuildResultType
import io.desolve.parser.compile.BuildStatus
import io.desolve.parser.compile.BuildTask
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.CompletableFuture
import kotlin.concurrent.thread

class GradleBuildTask(private vararg val arguments: GradlewArguments = arrayOf(GradlewArguments.Build)) : BuildTask
{
    override var status = BuildStatus.Starting

    override fun build(projectDirectory: File): CompletableFuture<BuildResult>
    {
        if (!projectDirectory.isDirectory)
        {
            status = BuildStatus.Failed
            throw IllegalArgumentException("projectDirectory must be a directory, not a file.")
        }

        return CompletableFuture.supplyAsync {
            status = BuildStatus.Started

            val process = ProcessBuilder(
                File(
                    projectDirectory,
                    "gradlew.bat"
                ).absolutePath,
                *arguments.map {
                    it.argument
                }.toTypedArray(),
            )
                .directory(projectDirectory)
                .start()

            val info = BufferedReader(
                InputStreamReader(
                    process.inputStream
                )
            )

            val error = BufferedReader(
                InputStreamReader(
                    process.errorStream
                )
            )


            thread {
                while (process.isAlive)
                {
                    val line = error.readLine()

                    if (line != null)
                    {
                        println(line)
                    }
                }
            }

            thread {
                while (process.isAlive)
                {
                    val line = info.readLine()

                    if (line != null)
                    {
                        println(line)
                    }
                }
            }

            println("heee2y")

            process.waitFor()


            val buildDirectory = File(projectDirectory, "/build/libs/")
            val file = scanForJar(buildDirectory)
                ?: return@supplyAsync BuildResult(
                    BuildResultType.Failed,
                    null
                )

            return@supplyAsync BuildResult(
                BuildResultType.Success,
                file
            ).apply {
                status = BuildStatus.Finished
            }
        }
    }

    private fun scanForJar(directory: File): File?
    {
        val files = directory.listFiles() ?: return null

        var largestFile: File? = null
        var largestSize: Long? = null

        for (file in files)
        {
            if (!file.name.endsWith(".jar"))
            {
                continue
            }

            val path = Paths.get(file.path)
            val size = Files.size(path)

            if (largestSize == null || size > largestSize)
            {
                largestFile = file
                largestSize = size
            }
        }

        return largestFile
    }
}

enum class GradlewArguments(val argument: String)
{
    Build("build"),
    Clean("clean"),
}