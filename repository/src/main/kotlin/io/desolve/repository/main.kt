package io.desolve.repository

import io.desolve.config.impl.EnvTableRepositoryConfig
import io.desolve.data.DependencyData
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.head
import io.ktor.server.routing.routing
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author Patrick Zondervan
 * @since 5/23/2022
 */
fun main()
{
    embeddedServer(Netty, port = 3717, module = Application::myApplicationModule).start(wait = true)
}

fun Route.depend()
{
    val parser = FileParser()

    get("/repo/{repository...}") {
        this.context.respondFile(parser.parseFile(this.context)?.file ?: return@get)
    }

    head("/repo/{repository...}") {
        val data = parser.parseFile(this.context) ?: return@head

        this.context.respondText(
            """
                file path on server: ${data.file.path}
                file size: ${Files.size(Paths.get(data.file.path))}
            """.trimIndent()
        )
    }
}

class FileParser
{
    suspend fun parseFile(call: ApplicationCall): DependencyData?
    {
        val entries = call.parameters.getAll("repository") ?: call.respondText("aaa")

        if (entries !is List<*>)
        {
            return null
        }

        val size = entries.size

        val parameterAmount = 4
        val groupIdLength = size - parameterAmount + 1

        var groupId = ""
        val artifactId = entries[groupIdLength]
        val version = entries[groupIdLength + 1]
        val fileName = entries[groupIdLength + 2]

        for (i in 0 until groupIdLength)
        {
            groupId += entries[i]

            if (i != groupIdLength - 1)
            {
                groupId += "."
            }
        }

        if (artifactId == null || version == null || fileName == null)
        {
            call.respond(
                HttpStatusCode.BadRequest,
                "Invalid parameters provided, some parameters are null: ${groupId}:${artifactId}:${version}"
            )
            return null
        }

        val config = EnvTableRepositoryConfig
        val data =
            DependencyData(groupId, artifactId as String, version as String, fileName as String, config.getBuildDirectory())

        if (!data.isFile())
        {
            call.respond(
                HttpStatusCode.BadRequest,
                "No proper jar file found with provided arguments.\n${data}"
            )
            return null
        }

        return data
    }

}

fun Application.myApplicationModule()
{
    routing {
        depend()
    }
}
