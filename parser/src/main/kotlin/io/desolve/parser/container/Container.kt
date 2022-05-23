package io.desolve.parser.container

import java.io.File

interface Container
{
    var status: ContainerStatus
    fun moveFolderToContainer(file: File)
    fun executeCommand(vararg arguments: String): DataStream
}

enum class ContainerStatus
{
    Busy,
    Idle
}