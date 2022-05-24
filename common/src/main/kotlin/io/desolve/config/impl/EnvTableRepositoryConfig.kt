package io.desolve.config.impl

import io.desolve.config.RepositoryConfig
import java.io.File

/**
 * @author Patrick Zondervan
 * @since 5/23/2022
 */
object EnvTableRepositoryConfig : RepositoryConfig
{
    override fun getDirectory(): File
    {
        val file = File("${System.getenv("USERPROFILE")}${File.separator}.krepositories")

        if (!file.exists())
        {
            file.mkdirs()
        }

        return file
    }
}