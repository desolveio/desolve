package io.desolve.parser.container.containerless

import io.desolve.parser.container.ContainerHandler

object ContainerlessContainerHandler : ContainerHandler<Containerless>
{
    override val containers = mutableListOf<Containerless>()

    override fun getAvailableContainer(): Containerless
    {
        return Containerless
    }

    override fun constructNewContainer(): Containerless
    {
        return Containerless
    }
}