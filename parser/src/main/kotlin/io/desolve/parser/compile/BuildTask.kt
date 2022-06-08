package io.desolve.parser.compile

import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * @author Patrick Zondervan
 * @since 5/23/2022
 */
interface BuildTask
{
    var status: BuildStatus
    val buildLog: MutableList<String>

    fun build(projectDirectory: File): CompletableFuture<BuildResult>
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
