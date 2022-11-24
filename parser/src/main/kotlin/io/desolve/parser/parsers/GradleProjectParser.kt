package io.desolve.parser.parsers

import io.desolve.parser.ParsedProject
import io.desolve.parser.ProjectParser
import io.desolve.parser.ProjectType
import io.desolve.parser.compile.type.GradleBuildTask
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.model.GradleModuleVersion
import java.io.File
import java.util.concurrent.CompletableFuture

object GradleProjectParser : ProjectParser
{
    override val projectType: ProjectType = ProjectType.Gradle

    override fun parse(directory: File): CompletableFuture<ParsedProject?>
    {
        return parse(directory, null)
    }

    override fun parse(directory: File, parent: ParsedProject?): CompletableFuture<ParsedProject?>
    {
        return CompletableFuture
            .supplyAsync {
                var groupId: String? = null
                var version: String? = null
                var artifactId: String? = null

                GradleConnector
                    .newConnector()
                    .forProjectDirectory(directory)
                    .connect()
                    .use { connection ->
                        val project = connection
                            .getModel(
                                GradleModuleVersion::class.java
                            )
                            ?: return@use

                        groupId = project.group
                        artifactId = project.name
                        version = project.version
                    }

                if (artifactId == null || version == null || groupId == null)
                {
                    if (parent == null)
                    {
                        throw IllegalStateException("parent is null, but artifactId, version, or groupId are also null.")
                    }

                    artifactId = artifactId ?: directory.name
                    version = version ?: parent.version
                    groupId = groupId ?: parent.groupId
                }

                val buildResult = buildProject(GradleBuildTask(), parent, directory) { it.build(directory) }
                    .join() ?: throw IllegalStateException("Unable to build project")

                return@supplyAsync parseFromResult(
                    groupId!!,
                    artifactId!!,
                    version!!,
                    buildResult,
                    directory,
                    parent
                ).join()
            }
    }
}
