package pl.polsl.sikorfalf

import com.auth0.jwt.JWT
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*



fun Application.configureRouting() {
    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()
    val myRealm = environment.config.property("jwt.realm").getString()
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/pozdrow") {
            call.respondText("Pozdrawiam!")
        }

    }
}
