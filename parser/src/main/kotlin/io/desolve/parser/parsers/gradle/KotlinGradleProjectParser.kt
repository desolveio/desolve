package io.desolve.parser.parsers.gradle

/**
 * @author Patrick Zondervan
 * @since 5/23/2022
 */
object KotlinGradleProjectParser : GradleProjectParser()
{
    override val buildFileName = "build.gradle.kts"
    override val settingsFileName = "settings.gradle.kts"
}