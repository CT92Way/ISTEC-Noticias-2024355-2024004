package pt.noticias.service

import io.ktor.client.statement.*
import pt.noticias.model.auth.LoginCredentials
import pt.noticias.model.auth.VerifyTokenRequest

/**
 * This interface defines the contract for interacting with the authentication service.
 */
interface AuthService {
    suspend fun login(credentials: LoginCredentials): HttpResponse
    suspend fun verifyToken(token: VerifyTokenRequest): HttpResponse
}