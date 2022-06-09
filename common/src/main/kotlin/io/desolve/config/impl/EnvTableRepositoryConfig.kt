package io.desolve.config.impl

import io.desolve.config.RepositoryConfig
import io.desolve.util.OSType
import org.apache.commons.io.FileUtils
import java.io.File

/**
 * @author Patrick Zondervan
 * @since 5/23/2022
 */
object EnvTableRepositoryConfig : RepositoryConfig
{
    override fun getDirectory(): File
    {
        val file = File(
            FileUtils.getUserDirectory(),
            ".desolve"
        )

        if (!file.exists())
            file.mkdirs()

        return file
    }
}
