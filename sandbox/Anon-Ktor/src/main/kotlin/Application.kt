package pl.polsl.sikorfalf

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
//import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.principal
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import java.util.Date

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}




fun Application.module() {

    @Serializable
    data class User(val username: String, val password: String)

    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()
    val realm = environment.config.property("jwt.realm").getString()
    configureSerialization()


    install(Authentication) {
        jwt("auth-jwt") {
            this.realm = realm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("username").asString().isNotEmpty()) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }

    routing {
        post("/login") {
            val user = call.receive<User>()

            if (user.username == "admin" && user.password == "admin") {
                val token = JWT.create()
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withClaim("username", user.username)
                    .withExpiresAt(Date(System.currentTimeMillis() + 60000)) // 1 min
                    .sign(Algorithm.HMAC256(secret))
                call.respond(mapOf("token" to token))
            } else {
                call.respondText("Invalid credentials")
            }
        }

        authenticate("auth-jwt") {
            get("/hello") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                call.respondText("Hello, $username! This is a protected endpoint.")
            }
        }

        post("/print") {
            val user = call.receive<User>()
            call.respondText( "Hello ${user.username} ${user.password}" )
        }

        get("/") {
            call.respondText("Public")
        }
    }
}

