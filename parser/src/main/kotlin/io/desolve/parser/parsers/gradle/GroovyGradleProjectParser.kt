package io.desolve.parser.parsers.gradle

/**
 * @author Patrick Zondervan
 * @since 5/23/2022
 */
object GroovyGradleProjectParser : GradleProjectParser()
{
    override val buildFileName = "build.gradle"
    override val settingsFileName = "settings.gradle"
}