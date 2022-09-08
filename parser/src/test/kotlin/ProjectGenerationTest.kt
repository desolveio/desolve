import io.desolve.config.impl.EnvTableRepositoryConfig
import io.desolve.parser.ParsedProject
import io.desolve.parser.compile.BuildResult
import io.desolve.parser.compile.BuildResultType
import org.junit.Test
import java.io.File

class ProjectGenerationTest
{
    fun generate()
    {
        val home = File("${System.getenv("USERPROFILE")}${File.separator}")

        val config =
            EnvTableRepositoryConfig

        val jarFile =
            File("${home}/projects/data-store-kt/build/libs/data-store-kt-1.0-SNAPSHOT.jar")

        ParsedProject(
            "io.github.yourmom", "data-stuff", "3.0", jarFile, config.getBuildDirectory(), BuildResult(BuildResultType.Failed, null)
        ).generateDirectory()
    }
}
