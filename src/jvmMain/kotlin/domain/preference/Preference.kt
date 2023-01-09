package domain.preference

import domain.preference.type.IntPreference
import domain.preference.type.PreferenceValue
import domain.preference.type.StringPreference

data class Preference(
    val name: String,
    val value: PreferenceValue
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

        fun parseFromLine(line: String): Preference {
            val pairs = line.split(',')
                .associate {
                    val (key, value) = it.split("=")
                    key to value
                }
            val value = pairs["value"]!!
            val preferenceValue = when (pairs["type"]!!) {
                "string" -> StringPreference(value)
                "int" -> IntPreference(value.toInt())
                else -> throw Exception("Preference read error: type not recognized")
            }
            return Preference(
                name = pairs["name"]!!,
                value = preferenceValue
            )
        }
    }
}
