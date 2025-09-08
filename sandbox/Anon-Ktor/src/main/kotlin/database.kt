package pl.polsl.sikorfalf

import org.jetbrains.exposed.sql.Database

fun initDatabase() {
    Database.connect(
        url = "jdbc:postgresql://localhost:5433/mydb",
        driver = "org.postgresql.Driver",
        user = "myuser",
        password = "mypassword"
    )
}