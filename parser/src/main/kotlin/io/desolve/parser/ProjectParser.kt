package io.desolve.parser

import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * @author Patrick Zondervan
 * @since 5/23/2022
 */
interface ProjectParser
{
    fun parse(directory: File): CompletableFuture<ParsedProject?>
}