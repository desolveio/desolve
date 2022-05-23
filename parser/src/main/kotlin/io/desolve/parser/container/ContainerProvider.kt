package io.desolve.parser.container

import io.desolve.parser.container.containerless.ContainerlessContainerHandler
import io.github.devrawr.inject.Inject
import io.github.devrawr.inject.inject

object ContainerProvider
{
    fun getContainerHandler(): ContainerHandler<*>
    {
        return try
        {
            val container by Inject.inject<ContainerHandler<*>>()
            container
        } catch (ignored: Exception)
        {
            ContainerlessContainerHandler
        }
    }
}