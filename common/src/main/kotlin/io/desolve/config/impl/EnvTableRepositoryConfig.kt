package io.desolve.config.impl

import io.desolve.config.RepositoryConfig
import io.desolve.util.OSType
import java.io.File

/**
 * @author Patrick Zondervan
 * @since 5/23/2022
 */
object EnvTableRepositoryConfig : RepositoryConfig
{
    override fun getDirectory(): File
    {
        val file = File("${System.getenv(
            when (OSType.resolveOsType())
            {
                OSType.Unix -> "HOME"
                OSType.Windows -> "USERPROFILE"
            }
        )}${File.separator}.dslvcache")

        if (!file.exists())
        {
            file.mkdirs()
        }

        return file
    }
}
