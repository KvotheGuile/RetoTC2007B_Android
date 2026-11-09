package mx.fer.app.model

import mx.fer.app.api.ApiService
import mx.fer.app.api.RetrofitClient


class AppRepository(
    private val api: ApiService = RetrofitClient.api
) {

    suspend fun getPosts(type: PostType? = null): Result<List<Post>> =
        runCatching {
            api.getPosts(type).sortedByDescending { it.publishedAt }
        }

    suspend fun getPost(id: String): Result<Post> =
        runCatching { api.getPost(id) }

    suspend fun getNotifications(): Result<List<Notification>> =
        runCatching {
            api.getNotifications().sortedByDescending { it.date }
        }
}