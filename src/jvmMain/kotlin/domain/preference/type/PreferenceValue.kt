package domain.preference.type

sealed interface PreferenceValue {
    val value: Any

    fun toSave(): String
}