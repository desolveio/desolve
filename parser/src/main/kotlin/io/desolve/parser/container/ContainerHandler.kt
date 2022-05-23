package io.desolve.parser.container

interface ContainerHandler<T : Container>
{
    val containers: MutableList<T>

    open fun getAvailableContainer(): T?
    {
        return containers.firstOrNull { it.status != ContainerStatus.Busy }
    }

    fun constructNewContainer(): T
}