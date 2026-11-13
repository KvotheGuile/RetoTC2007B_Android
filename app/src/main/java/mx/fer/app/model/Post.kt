package mx.fer.app.model

import java.util.Date


data class Post(
    val id: String,
    val title: String,
    val description: String,
    val linkImg: String?,
    val type: PostType,
    val publishedAt: Date,
    // Nullable on purpose: Gson ignores Kotlin defaults and leaves a missing field as null
    val attachments: List<Attachment>? = null
)

/** File linked from a post. For now only PDFs, opened by URL in the browser or a PDF viewer. */
data class Attachment(
    val name: String,
    val url: String
)
