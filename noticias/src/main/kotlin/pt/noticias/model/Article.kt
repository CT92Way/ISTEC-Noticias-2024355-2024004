package pt.noticias.model

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Article(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    var author: String? = null,
    var publishedDate: String? = null,
    val likes: Int = 0,
    val comments: List<Comment> = emptyList()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "title" to title,
        "content" to content,
        "author" to author,
        "publishedDate" to publishedDate,
        "likes" to likes,
        "comments" to comments.map { it.toMap() }
    )

    companion object {
        fun fromMap(data: Map<String, Any>): Article {
            val id = data["id"] as? String ?: UUID.randomUUID().toString()
            val title = data["title"] as String
            val content = data["content"] as String
            val author = data["author"] as? String
            val publishedDate = data["publishedDate"] as String
            val likes = (data["likes"] as? Number)?.toInt() ?: 0
            val comments = ((data["comments"] as? List<*>)?.map {
                Comment.fromMap(it as Map<String, Any>)
            } ?: emptyList())

            return Article(
                id = id,
                title = title,
                content = content,
                author = author,
                publishedDate = publishedDate,
                likes = likes,
                comments = comments
            )
        }
    }
}