package domain.preference

import domain.preference.type.BooleanPreference
import domain.preference.type.IntPreference
import domain.preference.type.PreferenceValue
import domain.preference.type.StringPreference

data class Preference<T : PreferenceValue>(
    val name: String,
    val value: T
) {
    override fun toString(): String {
        return "name=$name,value=$value"
    }

    companion object {

        fun parseNameFromLine(line: String): String {
            val startIndex = line.indexOfFirst { it == '=' } + 1
            val endIndex = line.indexOfFirst { it == ',' }
            return line.substring(startIndex, endIndex)
        }

        fun parseFromLine(line: String): Preference<PreferenceValue> {
            val pairs = line.split(',')
                .associate {
                    val (key, value) = it.split("=")
                    key to value
                }
            val value = pairs["value"]!!
            val preferenceValue = when (pairs["type"]!!) {
                "string" -> StringPreference(value)
                "int" -> IntPreference(value.toInt())
                "boolean" -> BooleanPreference(value.toBoolean())
                else -> throw Exception("Preference read error: type not recognized")
            }
            return Preference(
                name = pairs["name"]!!,
                value = preferenceValue
            )
        }
    }
}