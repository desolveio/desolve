package io.desolve.parser

import io.desolve.config.impl.EnvTableRepositoryConfig
import io.desolve.parser.parsers.gradle.GroovyGradleProjectParser
import io.desolve.parser.parsers.MavenProjectParser
import io.desolve.parser.parsers.gradle.KotlinGradleProjectParser
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
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

    data class RepositoryCloneSpec(
        val branch: String,
        val commit: String? = null,
        val credentials: Boolean = false,
        val credentialsProvider: Pair<String, String> = "" to "",
        val credentialsProviderType: RepositoryCloneCredentialProviders =
            RepositoryCloneCredentialProviders.None
    )

    enum class RepositoryCloneCredentialProviders(
        val overridingUsername: String? = null
    )
    {
        TokenGitHub("token"),
        TokenGitLab,
        Basic,
        None
    }

    fun parseFromRepository(
        url: String, spec: RepositoryCloneSpec =
            RepositoryCloneSpec(
                branch = "master"
            )
    ): CompletableFuture<Pair<ParsedProject?, File>>
    {
        val config = EnvTableRepositoryConfig

        val directory = File(
            config.getDirectory(),
            url.replace(":", "")
        )

        if (directory.exists())
            directory.deleteRecursively()

        return CompletableFuture.supplyAsync {
            try
            {
                Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(directory)
                    .setBranch(spec.branch)
                    .apply {
                        if (spec.credentials)
                        {
                            this.setCredentialsProvider(
                                UsernamePasswordCredentialsProvider(
                                    spec.credentialsProviderType.overridingUsername
                                        ?: spec.credentialsProvider.first,
                                    spec.credentialsProvider.second
                                )
                            )
                        }
                    }
                    .call()

                parseUnrecognizedDirectory(directory).join()
            } catch (exception: Exception)
            {
                exception.printStackTrace()

                if (directory.exists())
                {
                    directory.deleteRecursively()
                }

                throw exception
            } to directory
        }
    }
}
