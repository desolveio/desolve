package io.desolve.parser.container.docker

import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import io.desolve.parser.container.ContainerHandler
import java.time.Duration

object DockerContainerHandler : ContainerHandler<DockerContainer>
{
    override val containers = mutableListOf<DockerContainer>()
    var currentPort = 3718

    private val config: DefaultDockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
        .withDockerHost("tcp://localhost:${currentPort}")
        .build()

    private val client: ApacheDockerHttpClient = ApacheDockerHttpClient.Builder()
        .dockerHost(config.dockerHost)
        .sslConfig(config.sslConfig)
        .connectionTimeout(Duration.ofSeconds(10))
        .responseTimeout(Duration.ofMinutes(2))
        .build()

    override fun constructNewContainer(): DockerContainer
    {
        return DockerContainer(currentPort++, config, client).apply {
            containers.add(this)
        }
    }
}