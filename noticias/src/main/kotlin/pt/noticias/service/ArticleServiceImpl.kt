package pt.noticias.service

import pt.noticias.model.Article
import pt.noticias.model.Comment
import pt.noticias.model.Response
import pt.noticias.repository.ArticleRepository
import pt.noticias.repository.CommentRepository

class ArticleServiceImpl(
    private val articleRepository: ArticleRepository,
    private val commentRepository: CommentRepository
) : ArticleService {

    /**
     * Retrieves a list of all articles.
     *
     * @return A list of all articles.
     */
    override suspend fun getAllArticles(): List<Article> {
        val articles = articleRepository.getAllArticles()

        return articles.map { article ->
            val comments = commentRepository.getCommentsForArticle(article.id)
            article.copy(comments = comments)
        }
    }

    /**
     * Retrieves an article by its ID.
     *
     * @param id The ID of the article to retrieve.
     * @return The requested article, or null if not found.
     */
    override suspend fun getArticleById(id: String): Article? {
        val article = articleRepository.getArticle(id)
        article?.let {
            val comments = commentRepository.getCommentsForArticle(it.id)
            it.copy(comments = comments)
        }
        return article
    }

    /**
     * Creates a new article.
     *
     * @param article The article to be created.
     * @return The created article.
     */
    override suspend fun createArticle(article: Article): Article {
        return articleRepository.addArticle(article)
    }

    /**
     * Updates an existing article.
     *
     * @param id The ID of the article to update.
     * @param article The updated article data.
     * @return The updated article, or null if the article is not found.
     */
    override suspend fun updateArticle(id: String, article: Article): String? {
        return articleRepository.updateArticle(id, article)
    }

    /**
     * Deletes an existing article.
     *
     * @param id The ID of the article to delete.
     * @return True if the article was deleted successfully, false otherwise.
     */
    override suspend fun deleteArticle(id: String): Boolean {
        return articleRepository.deleteArticle(id)
    }

    /**
     * Likes an article.
     *
     * @param id The ID of the article to like.
     * @return A Response object indicating the success or failure of the operation.
     */
    override suspend fun addLike(id: String): Response {
        articleRepository.likeArticle(id)
        return Response("Like added successfully");
    }

    /**
     * Adds a comment to an article.
     *
     * @param articleId The ID of the article.
     * @param comment The comment to be added.
     * @return A Response object indicating the success or failure of the operation.
     */
    override suspend fun addComment(articleId: String, comment: Comment): Response {
        getArticleById(articleId) ?: throw IllegalArgumentException("Article not found")

        commentRepository.addComment(comment)

        return Response("Comment added successfully");
    }

    /**
     * Retrieves a list of comments for a given article.
     *
     * @param articleId The ID of the article.
     * @return A list of comments associated with the article.
     */
    override suspend fun getCommentsByArticleId(articleId: String): List<Comment> {
        return commentRepository.getCommentsForArticle(articleId)
    }
}