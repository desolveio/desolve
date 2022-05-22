package io.desolve.config

import java.io.File

interface RepositoryConfig
{
    fun getDirectory(): File
}