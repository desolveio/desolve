package io.desolve.config

import java.io.File

/**
 * @author Patrick Zondervan
 * @since 5/23/2022
 */
interface RepositoryConfig
{
    fun getDirectory(): File
    fun getBuildDirectory(): File
}
