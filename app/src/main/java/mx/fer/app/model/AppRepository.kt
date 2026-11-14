package mx.fer.app.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val pageSize = 20L

    // Publicaciones
    suspend fun getPosts(type: PostType? = null): Result<List<Post>> =
        runCatching {
            var query: Query = db.collection("publications")
                .whereEqualTo("status", "published")
            if (type != null) query = query.whereEqualTo("type", type.toFirestore())

            val snapshot = query
                .orderBy("publishedAt", Query.Direction.DESCENDING)
                .limit(pageSize)
                .get()
                .await()

            val now = Date()
            snapshot.documents
                .mapNotNull { it.toPost() }
                // respaldo: no mostrar vencidas aunque el panel aun no las marque
                .filter { post -> post.expiresAt == null || post.expiresAt.after(now) }
                .map { it.post }
        }

    suspend fun getPost(id: String): Result<Post> =
        runCatching {
            val doc = db.collection("publications").document(id).get().await()
            if (doc.getString("status") != "published") {
                throw NoSuchElementException("Post $id not found")
            }
            doc.toPost()?.post ?: throw NoSuchElementException("Post $id not found")
        }


    suspend fun registerView(postId: String): Result<Unit> =
        runCatching {
            val month = SimpleDateFormat("yyyy-MM", Locale.US).format(Date()) // ej. 2026-11
            db.collection("publications").document(postId).update(
                "views", FieldValue.increment(1),
                "viewsByMonth.$month", FieldValue.increment(1) // notacion con punto
            ).await()
            Unit
        }

    // Notificaciones
    suspend fun getNotifications(
        types: Set<PostType> = PostType.entries.toSet()
    ): Result<List<Notification>> =
        runCatching {
            if (types.isEmpty()) return@runCatching emptyList()

            val snapshot = db.collection("notifications")
                .whereIn("target", types.map { it.toFirestore() })
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(pageSize)
                .get()
                .await()

            snapshot.documents.map { doc ->
                Notification(
                    id = doc.id,
                    idPublication = doc.getString("publicationId"),
                    title = doc.getString("title") ?: "",
                    message = doc.getString("message") ?: "",
                    type = doc.getString("target").toPostType(),
                    date = doc.getTimestamp("date")?.toDate() ?: Date(0)
                )
            }
        }

    // Push (FCM)
    // Llamar al iniciar la app y cada vez que cambien las preferencias.
    suspend fun updateTopics(prefs: UserPreferences): Result<Unit> =
        runCatching {
            val messaging = FirebaseMessaging.getInstance()
            val wanted = buildSet {
                if (prefs.pushNotificationsEnabled) {
                    if (prefs.interestedGeneral) add("general")
                    if (prefs.interestedScholarship) add("scholars")
                    if (prefs.interestedApplicants) add("applicants")
                }
            }
            for (topic in listOf("general", "scholars", "applicants")) {
                if (topic in wanted) messaging.subscribeToTopic(topic).await()
                else messaging.unsubscribeFromTopic(topic).await()
            }
        }


    private class ParsedPost(val post: Post, val expiresAt: Date?)

    @Suppress("UNCHECKED_CAST")
    private fun DocumentSnapshot.toPost(): ParsedPost? {
        val title = getString("title") ?: return null
        val published: Timestamp = getTimestamp("publishedAt") ?: return null
        return ParsedPost(
            post = Post(
                id = id,
                title = title,
                description = getString("description") ?: "",
                linkImg = getString("linkImg")?.takeIf { it.isNotBlank() },
                type = getString("type").toPostType(),
                publishedAt = published.toDate()
            ),
            expiresAt = getTimestamp("expiresAt")?.toDate()
        )
    }

    private fun PostType.toFirestore(): String = when (this) {
        PostType.GENERAL -> "general"
        PostType.SCHOLARSHIP -> "scholars"
        PostType.APPLICANTS -> "applicants"
    }

    private fun String?.toPostType(): PostType = when (this) {
        "scholars" -> PostType.SCHOLARSHIP
        "applicants" -> PostType.APPLICANTS
        else -> PostType.GENERAL
    }
}