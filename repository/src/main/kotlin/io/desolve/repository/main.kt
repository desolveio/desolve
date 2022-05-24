package io.desolve.repository

import io.desolve.config.RepositoryConfig
import io.desolve.config.impl.EnvTableRepositoryConfig
import io.desolve.data.DependencyData
import io.github.devrawr.inject.Inject
import io.github.devrawr.inject.Injector
import io.github.devrawr.inject.inject

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author Patrick Zondervan
 * @since 5/23/2022
 */
fun main()
{
    Injector
        .create<RepositoryConfig>()
        .also {
            it.bind<RepositoryConfig>() to EnvTableRepositoryConfig
        }

    embeddedServer(Netty, port = 3717) {
        routing {
            depend()
        }
    }.start(wait = true)
}

fun Route.depend()
{
    get("/repo/{repository...}") {
        this.context.respondFile(parseFile(this.context)?.file ?: return@get)
    }

    head("/repo/{repository...}") {
        val data = parseFile(this.context) ?: return@head

        this.context.respondText(
            """
                file path on server: ${data.file.path}
                file size: ${Files.size(Paths.get(data.file.path))}
        """.trimIndent()
        )
    }
}

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

    val config by Inject.inject<RepositoryConfig>()
    val data =
        DependencyData(groupId, artifactId as String, version as String, fileName as String, config.getDirectory())

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