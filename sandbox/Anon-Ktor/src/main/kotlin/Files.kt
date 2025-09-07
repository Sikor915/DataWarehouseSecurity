package pl.polsl.sikorfalf

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.dao.id.IntIdTable

object Files : IntIdTable("files") {
    val fileName = text("filename")
    val filedata = blob("filedata")
    val anonymRules = blob("anonym_rules")
    val uploadedAt = timestamp("uploaded_at")
}
