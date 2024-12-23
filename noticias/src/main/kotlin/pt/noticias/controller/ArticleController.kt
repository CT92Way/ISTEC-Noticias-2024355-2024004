package pt.noticias.controller

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import pt.noticias.model.Article
import pt.noticias.model.Comment
import pt.noticias.model.auth.FirebaseUser
import pt.noticias.service.ArticleService
import java.text.SimpleDateFormat
import java.util.*

/**
 * This class defines routes for managing articles.
 *
 * @param articleService The injected ArticleService instance.
 */
class ArticleController(private val articleService: ArticleService) {

    /**
     * Defines routes for articles under the "/articles" path.
     */
    fun Route.articleRouting() {
        route("/articles") {
            /**
             * GET request handler for fetching all articles.
             */
            get {
                try {
                    val articles = articleService.getAllArticles()
                    call.respond(articles)
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, "Failed to fetch articles")
                }
            }

            /**
             * Protected route (requires "auth" authentication) for creating a new article.
             */
            authenticate("auth") {
                post {
                    try {
                        // Get the Firebase user from the call's attributes
                        val firebaseUser = call.attributes[AttributeKey<FirebaseUser>("firebase_user")]

                        val article = call.receive<Article>()
                        article.author = firebaseUser.email

                        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                        formatter.timeZone = TimeZone.getTimeZone("UTC")
                        article.publishedDate = formatter.format(Date())

                        val savedArticle = articleService.createArticle(article)
                        call.respond(HttpStatusCode.Created, savedArticle)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        call.respond(HttpStatusCode.InternalServerError, "Failed to create article: ${e.message}")
                    }
                }
            }

            /**
             * GET request handler for fetching a specific article by its ID.
             */
            get("{id}") {
                try {
                    val id = call.parameters["id"] ?: return@get call.respond(
                        HttpStatusCode.BadRequest, "Missing or malformed id"
                    )

                    articleService.getArticleById(id)?.let {
                        call.respond(it)
                    } ?: call.respond(HttpStatusCode.NotFound, "Article not found")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to fetch article")
                }
            }

            /**
             * Protected route (requires "auth" authentication) for updating an article.
             */
            authenticate("auth") {
                put("{id}") {
                    try {
                        val id = call.parameters["id"] ?: return@put call.respond(
                            HttpStatusCode.BadRequest, "Missing or malformed id"
                        )
                        val article = call.receive<Article>()

                        articleService.updateArticle(id, article)?.let {
                            call.respond(it)
                        } ?: call.respond(HttpStatusCode.NotFound, "Article not found")
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError, "Failed to update article")
                    }
                }
            }

            /**
             * Protected route (requires "auth" authentication) for deleting an article.
             */
            authenticate("auth") {
                delete("{id}") {
                    try {
                        val id = call.parameters["id"] ?: return@delete call.respond(
                            HttpStatusCode.BadRequest, "Missing or malformed id"
                        )

                        if (articleService.deleteArticle(id)) {
                            call.respond(HttpStatusCode.OK, "Article deleted")
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Article not found")
                        }
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError, "Failed to delete article")
                    }
                }
            }

            /**
             * Protected route (requires "auth" authentication) for adding comments to an article.
             */
            authenticate("auth") {
                post("{id}/comments") {
                    try {
                        val articleId = call.parameters["id"] ?: return@post call.respond(
                            HttpStatusCode.BadRequest, "Missing article id"
                        )

                        val firebaseUser = call.attributes[AttributeKey<FirebaseUser>("firebase_user")]

                        val commentRequest = call.receive<Comment>()
                        val comment = commentRequest.copy(articleId = articleId)
                        comment.author = firebaseUser.email

                        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                        formatter.timeZone = TimeZone.getTimeZone("UTC")
                        comment.timestamp = formatter.format(Date())

                        val savedComment = articleService.addComment(articleId, comment)
                        call.respond(HttpStatusCode.Created, savedComment)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError, "Failed to add comment: ${e.message}")
                    }
                }
            }


            /**
             * POST request handler for adding a like to a specific article by its ID.
             */
            post("{id}/like") {
                try {
                    val articleId = call.parameters["id"] ?: return@post call.respond(
                        HttpStatusCode.BadRequest, "Missing article id"
                    )

                    articleService.addLike(articleId)?.let {
                        call.respond(it)
                    } ?: call.respond(HttpStatusCode.NotFound, "Article not found")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to like article: ${e.message}")
                }
            }
        }
    }
}