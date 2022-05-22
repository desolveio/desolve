package io.github.devrawr.repository

import io.github.devrawr.desolver.config.RepositoryConfig
import io.github.devrawr.desolver.config.impl.EnvTableRepositoryConfig
import io.github.devrawr.desolver.data.DependencyData
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

fun main()
{
    Injector
        .create<RepositoryConfig>()
        .also {
            it.bind<RepositoryConfig>() to EnvTableRepositoryConfig
        }

    embeddedServer(Netty, port = 3713) {
        routing {
            depend()
        }
    }.start(wait = true)
}

fun Route.depend()
{
    get("/repo/{groupId}/{artifactId}/{version}/{file}") {
        this.context.respondFile(parseFile(this.context)?.file ?: return@get)
    }

    head("/repo/{groupId}/{artifactId}/{version}/{file}") {
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
    val groupId = call.parameters["groupId"]
    val artifactId = call.parameters["artifactId"]
    val version = call.parameters["version"]
    val fileName = call.parameters["file"]

    if (groupId == null || artifactId == null || version == null || fileName == null)
    {
        call.respond(
            HttpStatusCode.BadRequest,
            "Invalid parameters provided, some parameters are null: ${groupId}:${artifactId}:${version}"
        )
        return null
    }

    val config by Inject.inject<RepositoryConfig>()
    val data = DependencyData(groupId, artifactId, version, fileName, config.getDirectory())

    if (!data.isFile())
    {
        call.respond(
            HttpStatusCode.BadRequest,
            "No proper jar file found with provided arguments."
        )
        return null
    }

    return data
}