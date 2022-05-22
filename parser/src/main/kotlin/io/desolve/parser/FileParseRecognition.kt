package io.desolve.parser

import io.desolve.config.impl.EnvTableRepositoryConfig
import io.desolve.parser.parsers.GroovyGradleProjectParser
import io.desolve.parser.parsers.MavenProjectParser
import org.eclipse.jgit.api.Git
import java.io.File
import java.net.URL
import java.util.concurrent.CompletableFuture

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

    fun parseFromRepository(url: String): CompletableFuture<ParsedProject?>
    {
        val config = EnvTableRepositoryConfig
        val directory = File(config.getDirectory(), "/git/${url.replace(":", "")}")

        return CompletableFuture.supplyAsync {
            try
            {
                Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(directory)
                    .call()

                parseUnrecognizedDirectory(directory).apply {
                    directory.delete()
                }
            } catch (exception: Exception)
            {
                exception.printStackTrace()
                throw exception
            }
        }
    }
}