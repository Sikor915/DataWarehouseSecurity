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
                val userId = credential.payload.getClaim("userId").asInt()
                val trustLevel = credential.payload.getClaim("trustLevel").asInt()
                if (userId != null && trustLevel != null) {
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
                val userId = credential.payload.getClaim("userId").asInt()
                val trustLevel = credential.payload.getClaim("trustLevel").asInt()
                // traktujemy trustLevel >= 2 jako admin (przykład)
                if (userId != null && trustLevel != null && trustLevel >= 2) {
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
fun generateToken(config: JWTConfig, userId: Int, trustLevel: Int): String {
    return JWT.create()
        .withAudience(config.audience)
        .withIssuer(config.issuer)
        .withClaim("userId", userId)
        .withClaim("trustLevel", trustLevel)
        .withExpiresAt(Date(System.currentTimeMillis() + config.tokenExpiration))
        .sign(Algorithm.HMAC256(config.secret))
}
