import io.desolve.parser.FileParseRecognition
import org.junit.Test
import java.io.File

class ProjectRecognitionTest
{
    @Test
    fun gradleTest()
    {
        val home = File("${System.getenv("USERPROFILE")}${File.separator}")
        val projectDirectory = File(home, "/projects/packt")

        println(FileParseRecognition.parseUnrecognizedDirectory(projectDirectory).join()?.groupId)
    }
}
