package io.desolve.parser

import java.io.File

interface ProjectParser
{
    fun parse(directory: File): ParsedProject?
}