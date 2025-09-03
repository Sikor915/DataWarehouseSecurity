package pl.polsl.sikorfalf

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.CORS


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    initDatabase()

    install(CORS) {
        anyHost() // do testów lokalnych. W produkcji lepiej wpisz konkretny origin np. "http://localhost:3000"
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Options)
        allowHeader(HttpHeaders.Authorization)
    }

    val jwtConfig = environment.config.config("ktor.jwt").let {
        JWTConfig(
            realm = it.property("realm").getString(),
            secret = it.property("secret").getString(),
            issuer = it.property("issuer").getString(),
            audience = it.property("audience").getString(),
            tokenExpiration = it.property("expiry").getString().toLong()
        )
    }
    configureSerialization()
    configureAuthentication(config = jwtConfig)
    configureRouting(jwtConfig)
}

