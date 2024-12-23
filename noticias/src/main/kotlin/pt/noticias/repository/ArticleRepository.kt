package pt.noticias.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import pt.noticias.config.FirestoreConfig
import pt.noticias.model.Article
import pt.noticias.model.Comment
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * This class provides repository functions for interacting with articles in Firestore.
 */
class ArticleRepository {
    private val articleCollection = FirestoreConfig.getInstance().collection("articles")

    /**
     * Retrieves all articles from the "articles" collection.
     *
     * @return A list of all articles.
     * @throws Exception If an error occurs while fetching articles from Firestore.
     */
    suspend fun getAllArticles(): List<Article> = withContext(Dispatchers.IO) {
        try {
            val documents = articleCollection.get().get()
            return@withContext documents.map { Article.fromMap(it.data) }
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Retrieves a specific article by its ID from the "articles" collection.
     *
     * @param id The ID of the article to retrieve.
     * @return The requested article or null if not found.
     * @throws Exception If an error occurs while fetching the article from Firestore.
     */
    suspend fun getArticle(id: String): Article? = withContext(Dispatchers.IO) {
        try {
            val document = articleCollection.document(id).get().get()
            return@withContext if (document.exists()) {
                Article.fromMap(document.data!!)
            } else {
                null
            }
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Adds a new article to the "articles" collection.
     *
     * @param article The article to be added.
     * @return The added article.
     * @throws Exception If an error occurs while adding the article to Firestore.
     */
    suspend fun addArticle(article: Article): Article = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            try {
                articleCollection.document(article.id).set(article.toMap()).get()
                continuation.resume(article)
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    /**
     * Updates an existing article in the "articles" collection.
     *
     * @param id The ID of the article to update.
     * @param article The updated article data.
     * @return The updated article or null if the article is not found.
     * @throws Exception If an error occurs while updating the article in Firestore.
     */
    suspend fun updateArticle(id: String, article: Article): String? = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            try {
                val document = articleCollection.document(id)
                if (document.get().get().exists()) {
                    document.update(
                        mapOf("content" to article.content,
                            "title" to article.title
                        )
                    ).get()
                    continuation.resume("Article updated successfully")
                } else {
                    continuation.resume(null)
                }
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    /**
     * Deletes an article from the "articles" collection.
     *
     * @param id The ID of the article to delete.
     * @return True if the article is deleted successfully, false otherwise (e.g., if not found).
     * @throws Exception If an error occurs while deleting the article from Firestore.
     */
    suspend fun deleteArticle(id: String): Boolean = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            try {
                val document = articleCollection.document(id)
                if (document.get().get().exists()) {
                    document.delete().get()
                    continuation.resume(true)
                } else {
                    continuation.resume(false)
                }
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    /**
     * Increments the like count for an article in the "articles" collection.
     *
     * @param articleId The ID of the article to like.
     * @return The updated article with the incremented like count, or null if the article is not found.
     * @throws Exception If an error occurs while updating the article's like count in Firestore.
     */
    suspend fun likeArticle(articleId: String): Article? = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            try {
                val articleRef = articleCollection.document(articleId)
                val articleDoc = articleRef.get().get()

                if (!articleDoc.exists()) {
                    continuation.resume(null)
                    return@suspendCoroutine
                }

                val article = Article.fromMap(articleDoc.data!!)
                val updatedArticle = article.copy(likes = article.likes + 1)
                articleRef.set(updatedArticle.toMap()).get()
                continuation.resume(updatedArticle)
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }
}