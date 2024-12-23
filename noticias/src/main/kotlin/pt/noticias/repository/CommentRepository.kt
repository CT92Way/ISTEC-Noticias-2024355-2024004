package pt.noticias.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pt.noticias.config.FirestoreConfig
import pt.noticias.model.Comment
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * This class provides repository functions for interacting with comments in Firestore.
 */
class CommentRepository {
    private val commentCollection = FirestoreConfig.getInstance().collection("comments")

    /**
     * Adds a new comment to the "comments" collection.
     *
     * @param comment The comment to be added.
     * @return The added comment.
     * @throws Exception If an error occurs while adding the comment to Firestore.
     */
    suspend fun addComment(comment: Comment): Comment = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            try {
                commentCollection.document(comment.id).set(comment.toMap()).get()
                continuation.resume(comment)
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    /**
     * Retrieves all comments associated with a specific article from the "comments" collection.
     *
     * @param articleId The ID of the article for which to retrieve comments.
     * @return A list of comments for the given article.
     * @throws Exception If an error occurs while fetching comments from Firestore.
     */
    suspend fun getCommentsForArticle(articleId: String): List<Comment> = withContext(Dispatchers.IO) {
        try {
            val commentsQuery = commentCollection.whereEqualTo("articleId", articleId).get().get()
            commentsQuery.map { doc -> Comment.fromMap(doc.data) }
        } catch (e: Exception) {
            throw e
        }
    }
}