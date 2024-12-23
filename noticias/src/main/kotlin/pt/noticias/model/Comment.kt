package pt.noticias.model

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Comment(
    val id: String = UUID.randomUUID().toString(),
    var articleId: String? = null,
    var author: String? = null,
    val content: String,
    var timestamp: String? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "articleId" to articleId,
        "author" to author,
        "content" to content,
        "timestamp" to timestamp
    )

    companion object {
        fun fromMap(data: Map<String, Any>): Comment = Comment(
            id = data["id"] as String,
            articleId = data["articleId"] as String,
            author = data["author"] as String,
            content = data["content"] as String,
            timestamp = data["timestamp"] as String
        )
    }
}