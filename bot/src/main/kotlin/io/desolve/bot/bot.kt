package io.desolve.bot

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.reply
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import io.desolve.config.impl.EnvTableRepositoryConfig
import io.desolve.parser.FileParseRecognition
import io.github.devrawr.watcher.Watcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

suspend fun main()
{
    val bot = Kord("OTc3OTcxNzI0MzA4MDc4Njky.Gd8xti.OcB57AlVCnMDeyxDsom4nP4bH78BfuRUmtTioc")

    val guild = bot.guilds.first()
    val channel = guild.getChannel(Snowflake(977970949083246653)) as TextChannel
    val repoChannel = guild.getChannel(Snowflake(978352760946847814)) as VoiceChannel

    bot.on<MessageCreateEvent> {
        val content = message.content.split(" ")

        if (content[0] != "!repo" || message.author?.asMember(bot.guilds.first().id)?.getPermissions()
                ?.contains(Permission.Administrator) == false
        )
        {
            return@on
        }

        if (content.size < 2)
        {
            message.reply {
                this.content = "Provide a valid GitHub URL, such as: `https://github.com/patrickzondervan/scoreboards`"
            }
            return@on
        }

        val url = content[1]


        message.reply {
            this.content = "Trying to build from repository."
        }

        try
        {
            FileParseRecognition.parseFromRepository(url).thenAccept {

                runBlocking {
                    message.reply {
                        this.content = if (it == null)
                        {
                            "Was unable to build project from $url"
                        } else
                        {
                            it.generateDirectory()
                            "Succesfully built, added to repository."
                        }
                    }
                }
            }
        } catch (exception: Exception)
        {
            exception.printStackTrace()
        }
    }

    thread {
        Watcher.watchDirectory(EnvTableRepositoryConfig.getDirectory()) {
            if (it.name.startsWith("git"))
            {
                return@watchDirectory
            }

            runBlocking {
                val groupId = it.name
                val files = it.listFiles() ?: return@runBlocking

                for (file in files)
                {
                    val artifactId = file.name
                    val version = file.listFiles()!!.first().name

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

    }

    bot.login()
}