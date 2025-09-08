package pl.polsl.sikorfalf

import com.auth0.jwt.JWT
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.PipelineContext
import org.jetbrains.annotations.Debug
import pl.polsl.sikorfalf.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.statements.api.ExposedConnection
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import javax.sql.rowset.serial.SerialBlob
import java.sql.Blob
import java.sql.Connection
import javax.sql.DataSource
import java.io.File
import java.nio.file.Paths

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

        get("/datasets/names") {
            val datasets: List<Pair<String, String?>> = transaction {
                Files.slice(Files.fileName, Files.descript)
                    .selectAll()
                    .map { it[Files.fileName] to it[Files.descript]}
            }
            call.respond(datasets)
        }

        authenticate("jwt-auth") {
            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Niepoprawny token")
                    return@get
                }

                val user = transaction {
                    Users.select { Users.id eq userId }.singleOrNull()
                }

                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, "Użytkownik nie istnieje")
                    return@get
                }

                call.respond(
                    MeResponse(
                        firstName = user[Users.firstName],
                        lastName = user[Users.lastName],
                        trustLevel = user[Users.trustLevel]
                    )
                )
            }

            get("/datasets/download") {
                val name = call.parameters["name"] ?: return@get call.respondText(
                    "Missing name",
                    status = HttpStatusCode.BadRequest
                )

                val requiredTrust = call.parameters["trust"]?.toIntOrNull()
                    ?: return@get call.respondText(
                        "Missing or invalid trust level",
                        status = HttpStatusCode.BadRequest
                    )

                // extract user trust level from the JWT
                val principal = call.principal<JWTPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val userTrust = principal.payload.getClaim("trustLevel").asInt()

                // compare trust levels
                if (userTrust < requiredTrust) {
                    return@get call.respondText(
                        "Insufficient trust level",
                        status = HttpStatusCode.Forbidden
                    )
                }

                // only fetch the file if user passes trust check
                val file = transaction {
                    Files.select { Files.fileName eq name }
                        .singleOrNull()
                } ?: return@get call.respondText(
                    "File not found",
                    status = HttpStatusCode.NotFound
                )

                val yamlData = file[Files.anonymRules].bytes
                val csvData = file[Files.filedata].bytes

                File("sandbox/csv/data_policy.yaml").apply {
                    parentFile?.mkdirs()
                    writeBytes(yamlData)
                }

                File("sandbox/csv/healthcare_dataset.csv").apply {
                    parentFile?.mkdirs()
                    writeBytes(csvData)
                }

                val anonymized = main(requiredTrust)

                call.respondBytes(
                    anonymized,
                    contentType = ContentType.Text.CSV,
                    status = HttpStatusCode.OK
                )
            }
        }

        authenticate("jwt-auth-admin") {
            get("/users") {
                val tokenId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()

                val users = transaction {
                    Users.selectAll().map { row ->
                        mapOf(
                            "id" to row[Users.id].value,
                            "firstName" to row[Users.firstName],
                            "lastName" to row[Users.lastName],
                            "email" to row[Users.email],
                            "trustLevel" to row[Users.trustLevel]
                        )
                    }.filter { it["id"] != tokenId }
                }

                call.respond(users)
            }

            put("/users/{id}/trust") {
                val tokenId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                val userIdParam = call.parameters["id"]?.toIntOrNull()
                val body = call.receive<Map<String, Int>>()
                val newTrust = body["trustLevel"]

                if (userIdParam == null || newTrust == null) {
                    call.respondText("Invalid request", status = HttpStatusCode.BadRequest)
                    return@put
                }

                if (userIdParam == tokenId) {
                    call.respondText("Cannot change your own trust level", status = HttpStatusCode.Forbidden)
                    return@put
                }

                transaction {
                    Users.update({ Users.id eq userIdParam }) {
                        it[trustLevel] = newTrust
                    }
                }
                call.respondText("Trust level updated", status = HttpStatusCode.OK)
            }

            post("/files/upload") {
                val multipart = call.receiveMultipart()
                var csvBytes: ByteArray? = null
                var yamlBytes: ByteArray? = null
                var filename: String? = null
                var description: String? = null

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            when (part.name) {
                                "description" -> description = part.value
                                "filename" -> filename = part.value
                            }
                        }
                        is PartData.FileItem -> {
                            val bytes = part.streamProvider().readBytes()
                            when (part.name) {
                                "fileData" -> csvBytes = bytes
                                "anonymRules" -> yamlBytes = bytes
                            }
                        }
                        else -> Unit
                    }
                    part.dispose()
                }

                if (csvBytes == null || yamlBytes == null || filename.isNullOrBlank()) {
                    call.respondText(
                        "Missing CSV file, YAML file, or filename",
                        status = HttpStatusCode.BadRequest
                    )
                    return@post
                }



                transaction {
                    Files.insert {
                        it[fileName] = filename!!
                        it[descript] = description
                        it[filedata] = ExposedBlob(csvBytes)
                        it[anonymRules] = ExposedBlob(yamlBytes)
                    }
                }

                call.respondText("Files uploaded successfully", status = HttpStatusCode.OK)
            }

        }
    }
}
