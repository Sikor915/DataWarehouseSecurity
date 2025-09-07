package pl.polsl.sikorfalf

import org.jetbrains.exposed.sql.Table

import org.jetbrains.exposed.dao.id.IntIdTable

object Files : IntIdTable("files") {
    val fileName = text("filename")
    val filedata = text("filedata")
    val anonymRules = text("anonym_rules")
    val uploadedAt = text("uploaded_at")
}
