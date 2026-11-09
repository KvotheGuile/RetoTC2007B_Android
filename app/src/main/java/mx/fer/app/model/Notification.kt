package mx.fer.app.model

import java.util.Date

data class Notification(
    val id: String,
    val idPublication: String?,
    val title: String,
    val message: String,
    val type: PostType,
    val date: Date
)
