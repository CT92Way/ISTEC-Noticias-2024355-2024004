package pt.noticias.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class VerifyTokenResponse(
    val kind: String,
    val users: List<UserInfo>
)

@Serializable
data class UserInfo(
    val localId: String,
    val email: String,
    val passwordHash: String,
    val emailVerified: Boolean,
    val passwordUpdatedAt: Long,
    val providerUserInfo: List<ProviderUserInfo>,
    val validSince: String,
    val disabled: Boolean,
    val lastLoginAt: Long,
    val createdAt: Long
)

@Serializable
data class ProviderUserInfo(
    val providerId: String,
    val federatedId: String,
    val email: String,
    val rawId: String
)