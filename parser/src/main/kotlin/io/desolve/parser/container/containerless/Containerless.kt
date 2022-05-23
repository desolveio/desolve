package io.desolve.parser.container.containerless

import io.desolve.parser.container.Container
import io.desolve.parser.container.ContainerStatus
import io.desolve.parser.container.DataStream
import java.io.File

object Containerless : Container
{
    override var status = ContainerStatus.Idle
    var workingDirectory: File? = null

    override fun moveFolderToContainer(file: File)
    {
        workingDirectory = file
    }

    override fun moveFolderFromContainer(file: File)
    {
        workingDirectory = null
    }

    override fun executeCommand(vararg arguments: String): DataStream
    {
        val builder = ProcessBuilder(*arguments)
            .directory(workingDirectory)
            .start()

        return DataStream(
            inputStream = builder.inputStream,
            outputStream = builder.outputStream,
            errorStream = builder.errorStream
        ) {
            builder.waitFor()
        }
    }
}