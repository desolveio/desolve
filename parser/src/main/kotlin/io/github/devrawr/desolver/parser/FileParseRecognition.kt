package io.github.devrawr.desolver.parser

import java.io.File

object FileParseRecognition
{
    private val parsers = hashMapOf<ProjectType, ProjectParser>()

    fun parseUnrecognizedDirectory(directory: File): ParsedProject?
    {
        val type = ProjectType.recognize(directory) ?: return null
        val parser = parsers[type] ?: return null

        return parser.parse(directory)
    }
}