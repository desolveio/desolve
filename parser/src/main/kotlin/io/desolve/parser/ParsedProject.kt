package io.desolve.parser

import io.desolve.data.DependencyData
import java.io.File
import java.io.FileWriter
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*

class ParsedProject(
    val groupId: String,
    val artifactId: String,
    val version: String,
    val builtFile: File?,
    val parentDirectory: File?
)
{
    constructor(groupId: String, artifactId: String, version: String) : this(groupId, artifactId, version, null, null)

    fun generateDirectory(): File
    {
        if (parentDirectory == null || builtFile == null)
        {
            throw IllegalStateException("Unable to call #generateDirectory() from ParsedObject without builtFile/parentDirectory fields.")
        }

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
            var writer = FileWriter(File(data.directory, "${fileName}.pom"))

            writer.write(generatePom())
            writer.close()

            if (version.contains("SNAPSHOT"))
            {
                writer = FileWriter(File(data.directory, "maven-metadata.xml"))

                writer.write(generateMetadata())
                writer.close()
            }
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

    fun generateMetadata(): String
    {
        return """
        <?xml version="1.0" encoding="UTF-8"?>
        <metadata>
          <groupId>${groupId}</groupId>
          <artifactId>${artifactId}</artifactId>
          <versioning>
            <latest>${version}</latest>
            <release>${version}</release>
            <versions>
              <version>${version}</version>
            </versions>
            <lastUpdated>${SimpleDateFormat("yyyymmddhhMMss").format(Date())}</lastUpdated>
          </versioning>
        </metadata>
        """.trimIndent()
    }
}