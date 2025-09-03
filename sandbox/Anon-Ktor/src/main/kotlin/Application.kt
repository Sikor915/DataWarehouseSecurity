package pl.polsl.sikorfalf

import io.ktor.server.application.*


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    initDatabase()
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

