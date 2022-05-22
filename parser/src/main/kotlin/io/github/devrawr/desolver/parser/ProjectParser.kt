package io.github.devrawr.desolver.parser

import java.io.File

interface ProjectParser
{
    fun parse(directory: File): ParsedProject?
}