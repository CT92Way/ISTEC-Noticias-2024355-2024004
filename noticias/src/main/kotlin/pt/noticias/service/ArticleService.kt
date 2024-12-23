package pt.noticias.service

import pt.noticias.model.Article
import pt.noticias.model.Comment
import pt.noticias.model.Response

/**
 * This interface defines the contract for interacting with article-related ----services.
 */
interface ArticleService {
    suspend fun getAllArticles(): List<Article>
    suspend fun getArticleById(id: String): Article?
    suspend fun createArticle(article: Article): Article
    suspend fun updateArticle(id: String, article: Article): String?
    suspend fun deleteArticle(id: String): Boolean
    suspend fun addLike(id: String): Response
    suspend fun addComment(articleId: String, comment: Comment): Response
    suspend fun getCommentsByArticleId(articleId: String): List<Comment>
}