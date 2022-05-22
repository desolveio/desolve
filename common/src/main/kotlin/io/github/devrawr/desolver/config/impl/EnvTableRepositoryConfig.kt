package io.github.devrawr.desolver.config.impl

import io.github.devrawr.desolver.config.RepositoryConfig
import java.io.File

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