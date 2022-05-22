package io.desolve.parser

import io.desolve.parser.parsers.GroovyGradleProjectParser
import io.desolve.parser.parsers.MavenProjectParser
import java.io.File

object FileParseRecognition
{
    private val parsers = hashMapOf(
        ProjectType.Maven to MavenProjectParser,
        ProjectType.Gradle to GroovyGradleProjectParser
    )

    fun parseUnrecognizedDirectory(directory: File): ParsedProject?
    {
        val type = ProjectType.recognize(directory) ?: return null
        val parser = parsers[type] ?: return null

        return parser.parse(directory)
    }
}