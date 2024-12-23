package pt.noticias

import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import pt.noticias.config.FirestoreConfig
import pt.noticias.controller.ArticleController
import pt.noticias.controller.AuthController
import pt.noticias.http.AuthHttpClient
import pt.noticias.model.auth.FirebaseUser
import pt.noticias.model.auth.VerifyTokenRequest
import pt.noticias.model.auth.VerifyTokenResponse
import pt.noticias.repository.ArticleRepository
import pt.noticias.repository.CommentRepository
import pt.noticias.service.ArticleServiceImpl
import pt.noticias.service.AuthServiceImpl

/**
 * Entry point for the Noticias application.
 */
fun main() {
    FirestoreConfig.initialize("istec-noticias.json")

    embeddedServer(Netty, port = 8080) {
        module()
    }.start(wait = true)
}

/**
 * Configures the Ktor application modules.
 *
 * @receiver The Ktor application instance.
 */
fun Application.module() {

    // Initialize Article Service
    val articleRepository = ArticleRepository()
    val commentRepository = CommentRepository()
    val articleService = ArticleServiceImpl(articleRepository, commentRepository)
    val articleController = ArticleController(articleService)

    // Initialize Article Service
    val authClient = AuthHttpClient()
    val authService = AuthServiceImpl(authClient)
    val authController = AuthController(authService)

    install(ContentNegotiation) {
        json()
    }

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowCredentials = true
        anyHost()
    }

    install(Authentication) {
        basic("auth") {
            skipWhen { call ->
                val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                if (token == null) {
                    return@skipWhen false
                }

                try {
                    val isValid = runBlocking {
                        try {
                            val httpResponse = authService.verifyToken(VerifyTokenRequest(token))
                            val responseText = httpResponse.body<VerifyTokenResponse>()

                            if (responseText.users.isNotEmpty()) {
                                // Extract user information and store it in the call's attributes
                                val user = responseText.users.first()
                                call.attributes.put(
                                    AttributeKey("firebase_user"),
                                    FirebaseUser(
                                        email = user.email
                                    )
                                )
                                return@runBlocking true
                            }
                            return@runBlocking false
                        } catch (e: Exception) {
                            println("Token verification failed: ${e.message}")
                            return@runBlocking false
                        }
                    }
                    return@skipWhen isValid
                } catch (e: Exception) {
                    println("Error during token extraction or validation: ${e.message}")
                    return@skipWhen false
                }
            }
        }
    }

    routing {
        swaggerUI(path = "swagger", swaggerFile = "src/main/resources/openapi/documentation.yaml")
        with(articleController) {
            articleRouting()
        }

        with(authController) {
            authRouting()
        }
    }
}