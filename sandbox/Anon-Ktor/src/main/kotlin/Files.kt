package pl.polsl.sikorfalf

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.dao.id.IntIdTable

object Files : IntIdTable("files") {
    val fileName = text("filename")
    val descript = text("description").nullable()
    val filedata = blob("filedata")        // BYTEA w PostgreSQL
    val anonymRules = blob("anonym_rules") // BYTEA w PostgreSQ
    val uploadedAt = timestamp("uploaded_at")
}
