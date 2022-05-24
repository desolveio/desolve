package io.desolve.data

import java.io.File

/**
 * @author Patrick Zondervan
 * @since 5/23/2022
 */
data class DependencyData(
    val groupId: String,
    val artifactId: String,
    val version: String,
    val fileId: String,
    val parentDirectory: File
)
{
    val directory = File(parentDirectory, "${groupId}/${artifactId}/${version}")
    val file = File(parentDirectory, "${groupId}/${artifactId}/${version}/${fileId}")

    fun isFile(): Boolean
    {
        return file.exists() && !file.isDirectory
    }

    fun isDirectory(): Boolean
    {
        return directory.exists() && directory.isDirectory
    }
}