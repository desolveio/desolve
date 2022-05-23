package io.desolve.parser

import io.desolve.config.impl.EnvTableRepositoryConfig
import io.desolve.parser.parsers.GroovyGradleProjectParser
import io.desolve.parser.parsers.MavenProjectParser
import org.eclipse.jgit.api.Git
import java.io.File
import java.util.concurrent.CompletableFuture

object FileParseRecognition
{
    private val parsers = hashMapOf(
        ProjectType.Maven to MavenProjectParser,
        ProjectType.Gradle to GroovyGradleProjectParser
    )

    fun parseUnrecognizedDirectory(directory: File): CompletableFuture<ParsedProject?>
    {
        return CompletableFuture.supplyAsync {
            val type = ProjectType.recognize(directory) ?: return@supplyAsync null
            val parser = parsers[type] ?: return@supplyAsync null

            return@supplyAsync parser
                .parse(directory)
                .join()
        }
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

                parseUnrecognizedDirectory(directory).join().apply {
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