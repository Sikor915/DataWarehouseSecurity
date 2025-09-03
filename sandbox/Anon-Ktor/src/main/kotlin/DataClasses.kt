package pl.polsl.sikorfalf

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(val token: String)

@Serializable
data class MeResponse(
    val firstName: String,
    val lastName: String,
    val trustLevel: Int
)

