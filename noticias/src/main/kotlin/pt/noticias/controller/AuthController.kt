package pt.noticias.controller

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserRecord
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import pt.noticias.model.auth.AuthResponse
import pt.noticias.model.auth.LoginCredentials
import pt.noticias.model.auth.UserRegistrationRequest
import pt.noticias.service.AuthService

/**
 * This class defines routes for user authentication (login and registration).
 */
class AuthController(private val authService: AuthService) {

    /**
     * Defines routes for authentication under the "/auth" path.
     */
    fun Route.authRouting() {
/**
 * Handles user login requests.
 */
        post("/login") {
            val credentials = call.receive<LoginCredentials>()

            try {
                val loginCredentials = LoginCredentials(credentials.email, credentials.password)
                val response = authService.login(loginCredentials)

                when (response.status) {
                    HttpStatusCode.OK -> {
                        val authResponse = response.body<AuthResponse>()
                        call.respond(authResponse)
                    }

                    else -> {
                        call.respond(HttpStatusCode.InternalServerError, "Login failed")
                    }
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Login failed: ${e.message}")
            }
        }

/**
 * Handles user registration requests.
 */
        post("/register") {
            try {
                val user = call.receive<UserRegistrationRequest>()

                val auth = FirebaseAuth.getInstance()

                // Create a new user in Firebase Authentication
                val newUser = UserRecord.CreateRequest()
                    .setEmail(user.email)
                    .setPassword(user.password)
                val firebaseUser = auth.createUserAsync(newUser).get()

                // Respond with success message (or user details)
                call.respond(HttpStatusCode.Created, "User created successfully")

            } catch (e: FirebaseAuthException) {
                when (e.message) {
                    "EMAIL_EXISTS" -> call.respond(HttpStatusCode.Conflict, "Email already exists")
                    "INVALID_PASSWORD" -> call.respond(HttpStatusCode.BadRequest, "Invalid password")
                    else -> call.respond(HttpStatusCode.InternalServerError, "Failed to create user: ${e.message}")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create user: ${e.message}")
            }
        }
    }
}