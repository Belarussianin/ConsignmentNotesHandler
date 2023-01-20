package domain.preference

data class Preference<T>(
    val name: String,
    val value: PreferenceValue<T>
) {
    //constructor(name: String, value: T) : this(name, PreferenceValue(value))

    override fun toString(): String {
        return "name=$name,value=$value"
    }

    companion object {

        fun parseNameFromLine(line: String): String {
            val startIndex = line.indexOfFirst { it == '=' } + 1
            val endIndex = line.indexOfFirst { it == ',' }
            return line.substring(startIndex, endIndex)
        }

        fun parseFromLine(line: String): Preference<out Any> {
            val pairs = line.split(',')
                .associate {
                    val (key, value) = it.split("=")
                    key to value
                }
            val value = pairs["value"]!!
            val preferenceValue: Any = when (val type = pairs["type"]!!) {
                "string" -> value
                "int" -> value.toInt()
                "boolean" -> value.toBoolean()
                else -> throw Exception("Preference read error: type '$type' is not recognized")
            }
            return Preference(
                name = pairs["name"]!!,
                value = PreferenceValue(preferenceValue)
            )
        }
    }
}