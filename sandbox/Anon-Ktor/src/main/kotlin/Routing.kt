package pl.polsl.sikorfalf

import com.auth0.jwt.JWT
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.PipelineContext
import pl.polsl.sikorfalf.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

fun Application.configureRouting(config: JWTConfig) {
    routing {

        post("/register") {
            val request = call.receive<RegisterRequest>()
            val email = request.email.lowercase()

            val existingUser = transaction {
                Users.select { Users.email eq email }.singleOrNull()
            }

            if (existingUser != null) {
                call.respond(HttpStatusCode.Conflict, "Użytkownik o tym emailu już istnieje")
                return@post
            }

            // Hashowanie hasła
            val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())

            // Dodanie nowego użytkownika
            val userId = transaction {
                Users.insertAndGetId { row ->
                    row[firstName] = request.firstName
                    row[lastName] = request.lastName
                    row[Users.email] = email
                    row[passwordHash] = hashedPassword
                    row[trustLevel] = 1
                }.value
            }

            // Generowanie tokena JWT
            val token = generateToken(config, userId, 1)

            call.respond(HttpStatusCode.Created, mapOf("message" to "Rejestracja udana. Możesz się teraz zalogować."))
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            val email = request.email.lowercase()

            val user = transaction {
                Users.select { Users.email eq email }.singleOrNull()
            }

            if (user == null || !BCrypt.checkpw(request.password, user[Users.passwordHash])) {
                call.respond(HttpStatusCode.Unauthorized, "Niepoprawny email lub hasło")
                return@post
            }

            val userId = user[Users.id]
            val trustLevel = user[Users.trustLevel]

            val token = generateToken(config, userId.value, trustLevel)

            call.respond(HttpStatusCode.OK, AuthResponse(token))
        }
    }
}
