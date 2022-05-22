package io.desolve.parser

import java.io.File
import java.util.concurrent.CompletableFuture

interface ProjectParser
{
    fun parse(directory: File): CompletableFuture<ParsedProject?>
}