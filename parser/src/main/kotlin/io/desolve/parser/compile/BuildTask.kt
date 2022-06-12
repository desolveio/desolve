package io.desolve.parser.compile
import java.io.BufferedReader
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

/**
 * @author Patrick Zondervan
 * @since 5/23/2022
 */
interface BuildTask
{
    var status: BuildStatus
    val buildLog: MutableList<String>

    fun build(projectDirectory: File): CompletableFuture<BuildResult>

    fun logBuildDialogue(
        parentLog: MutableList<String>,
        stream: BufferedReader,
        error: BufferedReader,
        active: AtomicBoolean
    )
    {
        thread {
            while (active.get())
            {
                val line = error.readLine()

                if (line != null)
                {
                    this.buildLog += line
                }
            }
        }

        thread {
            while (active.get())
            {
                val line = stream.readLine()

                if (line != null)
                {
                    this.buildLog += line
                }
            }
        }
    }

    fun scanForJar(directory: File): File?
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

data class BuildResult(
    val result: BuildResultType,
    val file: File?,
    val logs: List<String> = mutableListOf()
)

enum class BuildResultType
{
    Failed,
    Success
}

enum class BuildStatus
{
    Starting,
    Started,
    Failed,
    Finished
}
