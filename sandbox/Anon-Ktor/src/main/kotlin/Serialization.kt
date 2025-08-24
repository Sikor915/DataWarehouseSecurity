package pl.polsl.sikorfalf



import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

/**
 * Setting up serialization
 */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        jackson {
        }
    }
}