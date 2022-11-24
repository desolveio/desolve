import io.desolve.parser.FileParseRecognition
import org.junit.Test

class GitPullTest
{
//    @Test
    fun pullGitHub()
    {
        val repo = "https://github.com/patrickzondervan/configs"
        val project = FileParseRecognition
            .parseFromRepository(repo)
            .join()

//        project!!.generateDirectory()
    }
}
