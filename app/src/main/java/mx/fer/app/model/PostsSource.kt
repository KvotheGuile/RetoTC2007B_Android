package mx.fer.app.model

import java.util.Date

/**
 * What PostViewModel needs from the data layer. AppRepository implements it with Firestore;
 * the unit tests use a fake, so they run without network or Firebase.
 */
interface PostsSource {
    suspend fun getPosts(type: PostType? = null): Result<List<Post>>
    suspend fun getPost(id: String): Result<Post>
    suspend fun registerView(postId: String): Result<Unit>
}

/* ---------- Pure rules, kept apart from Firebase so they can be tested ---------- */

/** Push topic names, one per channel. */
internal val ALL_TOPICS = listOf("general", "scholars", "applicants")

/** Topics the phone should be subscribed to: none when push is off, otherwise one per interest. */
internal fun topicsFor(prefs: UserPreferences): Set<String> = buildSet {
    if (prefs.pushNotificationsEnabled) {
        if (prefs.interestedGeneral) add("general")
        if (prefs.interestedScholarship) add("scholars")
        if (prefs.interestedApplicants) add("applicants")
    }
}

/** A post stays visible until its expiry date; no date means it never expires. */
internal fun isActive(expiresAt: Date?, now: Date): Boolean =
    expiresAt == null || expiresAt.after(now)

internal fun PostType.toFirestore(): String = when (this) {
    PostType.GENERAL -> "general"
    PostType.SCHOLARSHIP -> "scholars"
    PostType.APPLICANTS -> "applicants"
}

/** Unknown or missing values fall back to GENERAL so a bad document still shows up. */
internal fun String?.toPostType(): PostType = when (this) {
    "scholars" -> PostType.SCHOLARSHIP
    "applicants" -> PostType.APPLICANTS
    else -> PostType.GENERAL
}
