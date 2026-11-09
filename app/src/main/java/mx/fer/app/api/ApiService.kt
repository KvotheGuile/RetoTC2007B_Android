package mx.fer.app.api

import mx.fer.app.model.Notification
import mx.fer.app.model.Post
import mx.fer.app.model.PostType

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("posts")
    suspend fun getPosts(@Query("type") type: PostType? = null): List<Post>

    @GET("posts/{id}")
    suspend fun getPost(@Path("id") id: String): Post

    @GET("notifications")
    suspend fun getNotifications(): List<Notification>
}