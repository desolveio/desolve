package io.desolve.parser.container.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.transport.DockerHttpClient
import io.desolve.parser.container.Container
import io.desolve.parser.container.ContainerStatus
import io.desolve.parser.container.DataStream
import java.io.File

class DockerContainer(port: Int, config: DefaultDockerClientConfig, client: DockerHttpClient) : Container
{
    private val container: DockerClient
    private val containerId: String

    override var status = ContainerStatus.Idle

    init
    {
        this.container = DockerClientImpl.getInstance(config, client)
        this.containerId = "builder-${port}"
        this.container.startContainerCmd(containerId)
    }

    override fun moveFolderToContainer(file: File)
    {
        container.copyArchiveToContainerCmd(containerId)
            .withRemotePath(file.path)
            .exec()
    }

    override fun executeCommand(vararg arguments: String): DataStream
    {
        val cmd = container
            .execCreateCmd(containerId)
            .withCmd(*arguments)
            .withAttachStdin(true)
            .withAttachStderr(true)
            .withAttachStdout(true)
            .exec()

        val frame = container
            .execStartCmd(cmd.id)
            .withStdIn(System.`in`)
            .exec(ResultCallback.Adapter())

        return DataStream(
            inputStream = System.`in`,
            outputStream = System.`out`,
            errorStream = System.`in`
        ) {
            frame.awaitCompletion()
        }
    }
}