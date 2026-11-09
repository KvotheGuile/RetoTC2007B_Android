package mx.fer.app.model

data class UserPreferences(
    val name: String,
    val pushNotificationsEnabled: Boolean,
    val interestedScholarship: Boolean,
    val interestedApplicants: Boolean,
    val interestedGeneral: Boolean,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)

// Used only by the login screen to pick the initial defaults. It is not stored.
fun UserType.toDefaultPreferences(name: String) = UserPreferences(
    name = name,
    pushNotificationsEnabled = true,
    interestedScholarship = this != UserType.ASPIRANTE,  // Becario: on, Aspirante: off
    interestedApplicants = this != UserType.BECARIO,     // Aspirante: on, Becario: off
    interestedGeneral = true
)