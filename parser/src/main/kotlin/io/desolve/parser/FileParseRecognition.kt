package io.desolve.parser

import io.desolve.config.impl.EnvTableRepositoryConfig
import io.desolve.parser.parsers.gradle.GroovyGradleProjectParser
import io.desolve.parser.parsers.MavenProjectParser
import io.desolve.parser.parsers.gradle.KotlinGradleProjectParser
import org.eclipse.jgit.api.Git
import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * @author Patrick Zondervan
 * @since 5/23/2022
 */
object FileParseRecognition
{
    private val parsers = hashMapOf(
        ProjectType.Maven to MavenProjectParser,
        ProjectType.Gradle to GroovyGradleProjectParser,
        ProjectType.GradleKotlin to KotlinGradleProjectParser
    )

    fun parseUnrecognizedDirectory(directory: File): CompletableFuture<ParsedProject?>
    {
        return CompletableFuture.supplyAsync {
            val type = ProjectType.recognize(directory)
                ?: throw IllegalArgumentException("Unable to find project type for provided directory")

            val parser = parsers[type]
                ?: throw IllegalArgumentException("Unable to find parser for provided project type")

            return@supplyAsync parser
                .parse(directory)
                .join()
        }
    }

    fun parseFromRepository(url: String): CompletableFuture<ParsedProject?>
    {
        val config = EnvTableRepositoryConfig

        val directory = File(
            config.getDirectory(),
            "/cache/${url.replace(":", "")}"
        )

        return CompletableFuture.supplyAsync {
            try
            {
                Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(directory)
                    // TODO: 6/8/2022 allow user to specify branch maybe
                    //  .setBranch("main")
                    .call()

                parseUnrecognizedDirectory(directory)
                    .join()
                    .apply {
                        directory.delete()
                    }
            } catch (exception: Exception)
            {
                exception.printStackTrace()

                if (directory.exists())
                {
                    directory.delete()
                }

                throw exception
            }
        }
    }
}
