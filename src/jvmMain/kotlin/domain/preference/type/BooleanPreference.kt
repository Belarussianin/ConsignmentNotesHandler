package domain.preference.type

@JvmInline
value class BooleanPreference(override val value: Boolean) : PreferenceValue {
    override fun toSave(): String = "$value,type=boolean"
    override fun toString(): String = toSave()
}