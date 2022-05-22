import io.github.devrawr.desolver.parser.FileParseRecognition
import org.junit.Test
import java.io.File

class ProjectRecognitionTest
{
    @Test
    fun gradleTest()
    {
        val home = File("${System.getenv("USERPROFILE")}${File.separator}")
        val projectDirectory = File(home, "/projects/reachered")

        FileParseRecognition.parseUnrecognizedDirectory(projectDirectory)!!.generateDirectory()
    }
}