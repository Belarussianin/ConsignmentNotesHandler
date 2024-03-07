package experimental.preference

@JvmInline
value class PreferenceValue<T>(val value: T) {

    fun toSave(): String {
        val type = when (value) {
            is String -> "string"
            is Int -> "int"
            is Boolean -> "boolean"
            else -> throw Exception("Preference value type is not supported yet")
        }
        return "$value,type=$type"
    }

    override fun toString(): String = toSave()
}