package pl.polsl.sikorfalf

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import java.util.Date

data class JWTConfig(
    val realm: String,
    val secret: String,
    val issuer: String,
    val audience: String,
    val tokenExpiration: Long
)

fun Application.configureAuthentication(config: JWTConfig) {
    install(Authentication) {

        /**
         * Userspace auth.
         */
        jwt("jwt-auth") {
            realm = config.realm

            val jwtVerifier = JWT
                .require(Algorithm.HMAC256(config.secret))
                .withAudience(config.audience)
                .withIssuer(config.issuer)
                .build()

            verifier(jwtVerifier)

            validate { credential ->
                val username = credential.payload.getClaim("username").asString()
                val role = credential.payload.getClaim("role").asString()
                if(!username.isNullOrEmpty() && !role.isNullOrEmpty()) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ -> call.respond(HttpStatusCode.Unauthorized) }
        }
        /**
         * Admin space auth.
         */
        jwt("jwt-auth-admin") {
            realm = config.realm

            val jwtVerifier = JWT
                .require(Algorithm.HMAC256(config.secret))
                .withAudience(config.audience)
                .withIssuer(config.issuer)
                .build()

            verifier(jwtVerifier)

            validate { credential ->
                val username = credential.payload.getClaim("username").asString()
                val role = credential.payload.getClaim("role").asString()
                if(!username.isNullOrEmpty() && !role.isNullOrEmpty() && role == "admin") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ -> call.respond(HttpStatusCode.Unauthorized) }
        }
    }
}

/**
 * JWT token generation
 */
fun generateToken(config: JWTConfig, username: String, role: String): String {
    return JWT.create()
        .withAudience(config.audience)
        .withIssuer(config.issuer)
        .withClaim("username",username)
        .withClaim("role",role)
        .withExpiresAt(Date(System.currentTimeMillis() + config.tokenExpiration))
        .sign(Algorithm.HMAC256(config.secret))
}