package mx.fer.app.model

/**
 * Stored on the device only (RF-02). No personal data: the app never asks for a name (RF-09).
 */
data class UserPreferences(
    val pushNotificationsEnabled: Boolean,
    val interestedScholarship: Boolean,
    val interestedApplicants: Boolean,
    val interestedGeneral: Boolean,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)

/** Builds the initial preferences from the interests picked during onboarding. */
fun preferencesFromOnboarding(
    interests: Set<PostType>,
    pushNotificationsEnabled: Boolean
) = UserPreferences(
    pushNotificationsEnabled = pushNotificationsEnabled,
    interestedScholarship = PostType.SCHOLARSHIP in interests,
    interestedApplicants = PostType.APPLICANTS in interests,
    interestedGeneral = PostType.GENERAL in interests
)
