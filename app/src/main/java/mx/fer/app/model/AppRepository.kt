package mx.fer.app.model

import mx.fer.app.api.ApiService
import mx.fer.app.api.RetrofitClient

import kotlinx.coroutines.delay

class AppRepository(
    private val api: ApiService = RetrofitClient.api
) {

    // Switch to false when your real server is ready
    private val useDummyData = true

    suspend fun getPosts(type: PostType? = null): Result<List<Post>> =
        runCatching {
            if (useDummyData) {
                delay(800) // simulate network latency
                DummyData.posts
                    .filter { type == null || it.type == type }
                    .sortedByDescending { it.publishedAt }
            } else {
                api.getPosts(type).sortedByDescending { it.publishedAt }
            }
        }

    suspend fun getPost(id: String): Result<Post> =
        runCatching {
            if (useDummyData) {
                delay(400)
                DummyData.posts.firstOrNull { it.id == id }
                    ?: throw NoSuchElementException("Post $id not found")
            } else {
                api.getPost(id)
            }
        }

    suspend fun getNotifications(): Result<List<Notification>> =
        runCatching {
            if (useDummyData) {
                delay(500)
                DummyData.notifications.sortedByDescending { it.date }
            } else {
                api.getNotifications().sortedByDescending { it.date }
            }
        }
}