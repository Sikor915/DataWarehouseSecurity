package pl.polsl.sikorfalf

import org.jetbrains.exposed.sql.Table

import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable("users") {
    val firstName = text("first_name")
    val lastName = text("last_name")
    val email = text("email").uniqueIndex()
    val passwordHash = text("password_hash")
    val trustLevel = integer("trust_level").default(1)
}
