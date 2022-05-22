package io.desolve.parser.compile

import java.io.File
import java.util.concurrent.CompletableFuture

interface BuildTask
{
    var status: BuildStatus

    fun build(projectDirectory: File): CompletableFuture<BuildResult>
}

data class BuildResult(
    val result: BuildResultType,
    val file: File?
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