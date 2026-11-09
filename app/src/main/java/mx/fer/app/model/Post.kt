package mx.fer.app.model

import java.util.Date


data class Post(
    val id: String,
    val title: String,
    val description: String,
    val linkImg: String?,
    val type: PostType,
    val publishedAt: Date
)
