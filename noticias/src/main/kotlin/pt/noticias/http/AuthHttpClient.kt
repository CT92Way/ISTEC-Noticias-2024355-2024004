package pt.noticias.http

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pt.noticias.model.auth.LoginCredentials
import pt.noticias.model.auth.VerifyTokenRequest

/**
 * This class provides a HTTP client for interacting with the Firebase Authentication API.
 */
class AuthHttpClient {
    /**
     * The underlying Ktor HTTP client instance configured with JSON content negotiation.
     */
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    /**
     * Sends a login request to the Firebase Authentication API using the provided credentials.
     *
     * @param credentials The login credentials (email and password).
     * @return The HTTP response from the Firebase Authentication API.
     * @throws HttpRequestException If an error occurs during the HTTP request.
     */
    suspend fun login(credentials: LoginCredentials): HttpResponse {
        return client.post("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=AIzaSyDo07rp-TkUgvLuX_S0ma8O_HI6naK66ZU") {
            contentType(ContentType.Application.Json)
            setBody(credentials)
        }
    }

    /**
     * Sends a request to verify a token with the Firebase Authentication API.
     *
     * @param token The token to be verified.
     * @return The HTTP response from the Firebase Authentication API.
     * @throws HttpRequestException If an error occurs during the HTTP request.
     */
    suspend fun verifyToken(token: VerifyTokenRequest): HttpResponse {
        return client.post("https://identitytoolkit.googleapis.com/v1/accounts:lookup?key=AIzaSyDo07rp-TkUgvLuX_S0ma8O_HI6naK66ZU") {
            contentType(ContentType.Application.Json)
            setBody(token)
        }
    }

}

