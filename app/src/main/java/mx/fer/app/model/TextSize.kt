package mx.fer.app.model

/**
 * Extra text size on top of the phone's own font size setting (it multiplies, never replaces it).
 * The theme caps the result at 200 % so cards don't break when the phone is already at its maximum.
 */
enum class TextSize(val label: String, val factor: Float) {
    SYSTEM("Como mi celular", 1f),
    LARGE("Grande", 1.15f),
    EXTRA_LARGE("Muy grande", 1.3f)
}
