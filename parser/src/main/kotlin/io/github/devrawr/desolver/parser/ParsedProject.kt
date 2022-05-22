package io.github.devrawr.desolver.parser

import io.github.devrawr.desolver.data.DependencyData
import java.io.File
import java.io.FileWriter

class ParsedProject(
    val groupId: String,
    val artifactId: String,
    val version: String,
    val builtFile: File,
    val parentDirectory: File
)
{
    fun generateDirectory(): File
    {
        val fileName = "${artifactId}-${version}"
        val data = DependencyData(
            groupId, artifactId, version, "${fileName}.jar", parentDirectory
        )

        if (!data.isDirectory())
        {
            data.directory.mkdirs()

            builtFile.copyTo(
                File(data.directory, "${fileName}.jar")
            )

            // writer for generating <fileName>.pom
            val writer = FileWriter(File(data.directory, "${fileName}.pom"))

            writer.write(generatePom())
            writer.close()
        }

        return data.directory
    }

    fun generatePom(): String
    {
        return """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <groupId>${groupId}</groupId>
                <artifactId>${artifactId}</artifactId>
                <version>${version}</version>
            </project>
        """.trimIndent()
    }
}