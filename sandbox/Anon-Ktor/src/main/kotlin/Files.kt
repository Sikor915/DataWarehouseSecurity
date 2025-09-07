package pl.polsl.sikorfalf

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.dao.id.IntIdTable

object Files : IntIdTable("files") {
    val fileName = text("filename")
    val filedata = binary("filedata")        // BYTEA w PostgreSQL
    val anonymRules = binary("anonym_rules") // BYTEA w PostgreSQL
    val description = text("description").nullable()
    val uploadedAt = timestamp("uploaded_at")
}
