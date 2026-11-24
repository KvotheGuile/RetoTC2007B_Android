package mx.fer.app.model

// Firestore version of the repo, same functions the ViewModels were already using

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : PostsSource {

    // Max docs per query, keeps reads (and cost) low
    private val pageSize = 20L

    //Posts

    // Always filter by status == "published" or the rules reject the query
    // Needs indexes status+publishedAt and status+type+publishedAt (Logcat gives the link)
    override suspend fun getPosts(type: PostType?): Result<List<Post>> =
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
                // In case the panel hasn't marked them as expired yet
                .filter { isActive(it.expiresAt, now) }
                .map { it.post }
        }

    override suspend fun getPost(id: String): Result<Post> =
        runCatching {
            val doc = db.collection("publications").document(id).get().await()
            if (doc.getString("status") != "published") {
                throw NoSuchElementException("Post $id not found")
            }
            doc.toPost()?.post ?: throw NoSuchElementException("Post $id not found")
        }

    // Call once when a post is opened: views +1 and viewsByMonth +1 in the same write
    override suspend fun registerView(postId: String): Result<Unit> =
        runCatching {
            val month = SimpleDateFormat("yyyy-MM", Locale.US).format(Date()) // e.g. 2026-11
            db.collection("publications").document(postId).update(
                "views", FieldValue.increment(1),
                "viewsByMonth.$month", FieldValue.increment(1) // dot notation so it doesn't overwrite other months
            ).await()
            Unit
        }

    //Usage

    // Call once per app start, the panel uses it to estimate the Firebase usage
    suspend fun registerAppOpen(): Result<Unit> =
        runCatching {
            val day = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) // e.g. 2026-11-22
            db.collection("usage").document(day)
                .set(mapOf("appOpens" to FieldValue.increment(1)), SetOptions.merge())
                .await()
            Unit
        }

    //Notifications

    // types = user's interests (all three by default). Index: target + date desc
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

    //Push

    // Subscribes to the user's topics and unsubscribes from the rest
    suspend fun updateTopics(prefs: UserPreferences): Result<Unit> =
        runCatching {
            val messaging = FirebaseMessaging.getInstance()
            val wanted = topicsFor(prefs)
            for (topic in ALL_TOPICS) {
                if (topic in wanted) messaging.subscribeToTopic(topic).await()
                else messaging.unsubscribeFromTopic(topic).await()
            }
        }

    //Firestore -> models

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
                publishedAt = published.toDate(),
                attachments = (get("documents") as? List<Map<String, Any>>)
                    ?.mapNotNull { d ->
                        val url = d["url"] as? String ?: return@mapNotNull null
                        Attachment(name = d["name"] as? String ?: "Documento", url = url)
                    }
            ),
            expiresAt = getTimestamp("expiresAt")?.toDate()
        )
    }
}
