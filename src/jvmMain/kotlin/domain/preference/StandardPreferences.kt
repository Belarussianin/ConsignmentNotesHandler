package domain.preference

import data.excel.data.ConsignmentNotesHandler

sealed class StandardPreferences<T : Any>(val name: String, open val value: T) {
    class ConsignmentPath(
        override val value: String = ConsignmentNotesHandler.defaultConsignmentsDirectory
    ) : StandardPreferences<String>("consignmentPath", value) {
        companion object {
            @JvmField
            val default = ConsignmentPath()
        }
    }

    class ResultPath(
        override val value: String = ConsignmentNotesHandler.defaultResultDirectory
    ) : StandardPreferences<String>("resultPath", value) {
        companion object {
            @JvmField
            val default = ResultPath()
        }
    }

    class IsDarkTheme(
        override val value: Boolean = false
    ) : StandardPreferences<Boolean>("isDarkTheme", value) {
        companion object {
            @JvmField
            val default = IsDarkTheme()
        }
    }

    class IsEnglishLanguage(
        override val value: Boolean = false
    ) : StandardPreferences<Boolean>("isEnglishLanguage", value) {
        companion object {
            @JvmField
            val default = IsEnglishLanguage()
        }
    }

    fun toPreference(): Preference<T> = Preference(name, PreferenceValue(value))
}