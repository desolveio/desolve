package io.desolve.parser.parsers.gradle

import io.desolve.parser.ProjectType

/**
 * @author Patrick Zondervan
 * @since 5/23/2022
 */
object GroovyGradleProjectParser : GradleProjectParser()
{
    override val projectType = ProjectType.Gradle
    override val buildFileName = "build.gradle"
    override val settingsFileName = "settings.gradle"
}