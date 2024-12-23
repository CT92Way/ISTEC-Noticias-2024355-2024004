package pt.noticias.service

import io.ktor.client.statement.*
import pt.noticias.http.AuthHttpClient
import pt.noticias.model.auth.LoginCredentials
import pt.noticias.model.auth.VerifyTokenRequest

class AuthServiceImpl(
    private val client: AuthHttpClient,
) : AuthService {

    /**
     * Performs user login.
     *
     * @param credentials The login credentials (email and password).
     * @return The HTTP response from the authentication service.
     */
    override suspend fun login(credentials: LoginCredentials): HttpResponse {
        return client.login(credentials)
    }

    /**
     * Verifies a user's authentication token.
     *
     * @param token The token to be verified.
     * @return The HTTP response from the authentication service.
     */
    override suspend fun verifyToken(token: VerifyTokenRequest): HttpResponse {
        return client.verifyToken(token)
    }
}