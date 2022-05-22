package io.desolve.parser.compile.type

import io.desolve.parser.compile.BuildResult
import io.desolve.parser.compile.BuildResultType
import io.desolve.parser.compile.BuildStatus
import io.desolve.parser.compile.BuildTask
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.concurrent.CompletableFuture

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
                    it.name
                }.toTypedArray(),
            )
                .directory(projectDirectory)
                .start()

            val output = BufferedReader(
                InputStreamReader(
                    process.inputStream
                )
            )

            process.waitFor()

            var line = output.readLine()

            while (line != null)
            {
                println(line)
                line = output.readLine()
            }

            val buildDirectory = File(projectDirectory, "/build/lib/")
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

        for (file in files)
        {
            if (!file.name.endsWith(".jar"))
            {
                continue
            }

            return file
        }

        return null
    }
}

enum class GradlewArguments(val argument: String)
{
    Build("build"),
    Clean("clean"),
}