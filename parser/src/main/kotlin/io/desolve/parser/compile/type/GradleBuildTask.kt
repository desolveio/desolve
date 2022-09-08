package io.desolve.parser.compile.type

import io.desolve.parser.compile.BuildResult
import io.desolve.parser.compile.BuildResultType
import io.desolve.parser.compile.BuildStatus
import io.desolve.parser.compile.BuildTask
import io.desolve.parser.container.ContainerProvider
import io.desolve.util.OSType
import org.apache.commons.io.FilenameUtils
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author Patrick Zondervan
 * @since 5/23/2022
 */
enum class GradlewArguments(val argument: String)
{
    Build("build"),
    Clean("clean"),
}

class GradleBuildTask(
    private vararg val arguments: GradlewArguments = arrayOf(GradlewArguments.Build),
    override val buildLog: MutableList<String> = mutableListOf()
) : BuildTask
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

            val containerHandler = ContainerProvider.getContainerHandler()
            val container = containerHandler.getAvailableContainer()
                ?: containerHandler.constructNewContainer()

            container.moveFolderToContainer(projectDirectory)

            val executableFile = when (OSType.resolveOsType())
            {
                OSType.Windows -> "gradlew.bat"
                OSType.Unix -> "gradlew"
            }

            val path = FilenameUtils
                .separatorsToSystem(
                    File(projectDirectory, executableFile).absolutePath
                )

            Runtime.getRuntime()
                .exec(
                    "chmod -R 777 $path"
                )
                .waitFor()

            val process =
                container.executeCommand(
                    path, *arguments.map { it.argument }.toTypedArray()
                )

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

            val alive = AtomicBoolean(true)
            logBuildDialogue(buildLog, info, error, alive)

            process.wait()
            alive.set(false)

            // once it's done compiling we'll want it to move back out of the container
            container.moveFolderFromContainer(projectDirectory)

            val buildDirectory = File(projectDirectory, "/build/libs/")
            println(buildDirectory.path)
            println(buildDirectory.listFiles())

            buildDirectory.walkTopDown().forEach {
                println(it.name)
            }

            val file = scanForJar(buildDirectory)
                ?: return@supplyAsync BuildResult(
                    BuildResultType.Failed,
                    null, buildLog.apply {
                        this.add("No archive found!")
                    }
                )

            return@supplyAsync BuildResult(
                BuildResultType.Success,
                file, this.buildLog
            ).apply {
                status = BuildStatus.Finished
            }
        }
    }
}
