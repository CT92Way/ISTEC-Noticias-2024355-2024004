package pt.noticias.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val idToken: String
)