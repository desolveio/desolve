package io.desolve.parser.container

import io.desolve.parser.container.containerless.ContainerlessContainerHandler

object ContainerProvider
{
    fun getContainerHandler(): ContainerHandler<*>
    {
        // TODO: 5/25/2022 ??? what is providing
        //  ContainerHandler ??? where to init koin ???

        // val container by Inject.inject<ContainerHandler<*>>()
        // container

        return ContainerlessContainerHandler
    }
}
