package pt.noticias.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginCredentials(val email: String, val password: String)