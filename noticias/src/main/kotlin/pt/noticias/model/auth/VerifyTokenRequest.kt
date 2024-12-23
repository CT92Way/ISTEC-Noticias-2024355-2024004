package pt.noticias.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class VerifyTokenRequest(val idToken: String)