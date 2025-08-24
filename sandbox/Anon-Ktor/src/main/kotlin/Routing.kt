package pl.polsl.sikorfalf

import com.auth0.jwt.JWT
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*



fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/pozdrow") {
            call.respondText("Pozdrawiam!")
        }

    }
}
