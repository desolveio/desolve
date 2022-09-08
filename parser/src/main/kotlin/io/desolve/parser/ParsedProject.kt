package io.desolve.parser

import io.desolve.data.DependencyData
import io.desolve.parser.compile.BuildResult
import java.io.File
import java.io.FileWriter
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple representation of how a Maven project is
 * structured, to be able to process this from any other
 * build tool required.
 *
 * Should most likely not be called manually, but instead by
 * one of our [ProjectParser], but if you want to, be my guest, it should work.
 *
 * 2 fields, [parentDirectory] and [builtFile] are not required fields,
 * but will be required to call the [generateDirectory] method, otherwise an [IllegalStateException] will be thrown.
 *
 * @author Patrick Zondervan
 * @since 5/23/2022
 */
class ParsedProject(
    val groupId: String,
    var artifactId: String,
    val version: String,
    val builtFile: File? = null,
    val parentDirectory: File? = null,
    val result: BuildResult,
    val parent: ParsedProject? = null,
)
{
    var children: MutableList<ParsedProject> = mutableListOf()
        set(value)
        {
            if (value.isNotEmpty())
            {
                artifactId += "-parent"
            }

            field = value
        }

    init
    {
        if (parent != null)
        {
            artifactId = "${parent.artifactId}-${artifactId}"
        }
    }

    /**
     * Generate the actual directory which will be used
     * by maven to retrieve the data for the dependency from.
     *
     * Dependency won't auto build, will have to invoke this manually.
     *
     * @param targetDirectory the directory this will be built in, and if this is null,
     *                        we will use the [parentDirectory] value.
     * @return the generated file
     */
    fun generateDirectory(targetDirectory: File? = parentDirectory): File
    {
        if (targetDirectory == null || builtFile == null || !targetDirectory.exists() || !builtFile.exists())
        {
            throw IllegalStateException("Unable to call #generateDirectory() from ParsedObject without builtFile/parentDirectory fields.")
        }

        val fileName = "${artifactId}-${version}"
        val data = DependencyData(
            groupId, artifactId, version, "${fileName}.jar", targetDirectory
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

        for (child in children)
        {
            child.generateDirectory()
        }

        return data.directory
    }

    /**
     * This will simply generate the POM file as a string,
     * using the data in the class.
     *
     * This will be required within the maven repository itself,
     * and should be accessible at the same endpoint as the JAR.
     *
     * @return the generated text to write to the file
     */
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

    /**
     * This will generate metadata required for projects which have
     * certain keywords (afaik, just "SNAPSHOT") in it's version.
     *
     * Maven will request this file upon finding the "SNAPSHOT" keyword
     * in the version, from the same endpoint as the .pom and the .jar
     *
     * @return the generated text to write to the file
     */
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
