import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.channel.TextChannel
import io.desolve.config.impl.EnvTableRepositoryConfig
import io.github.devrawr.watcher.Watcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

suspend fun main()
{
    val bot = Kord("OTc3OTcxNzI0MzA4MDc4Njky.Gd8xti.OcB57AlVCnMDeyxDsom4nP4bH78BfuRUmtTioc")
    val channel = bot.guilds.first().getChannel(Snowflake(977970949083246653)) as TextChannel

    thread {
        Watcher.watchDirectory(EnvTableRepositoryConfig.getDirectory()) {
            runBlocking {
                val groupId = it.name
                val artifactId = it.listFiles()!!.first().name
                val version = it.listFiles()!!.first().listFiles()!!.first().name

                channel.createEmbed {
                    this.title = "New Project Found"
                    this.field("Group Id", true) {
                        groupId
                    }

                    this.field("Artifact Id", true) {
                        artifactId
                    }

                    this.field("Version", true) {
                        version
                    }

                    this.field("build.gradle example") {
                        """
                            ```groovy
                            implementation "${groupId}:${artifactId}:${version}"
                            ```
                        """.trimIndent()
                    }

                    this.field("pom.xml example") {
                        """
                            ```xml
                            <dependency>
                                <groupId>${groupId}</groupId>
                                <artifactId>${artifactId}</artifactId>
                                <version>${version}</version>
                            </dependency>
                            ```
                        """.trimIndent()
                    }
                }
            }
        }

    }

    bot.login()
}