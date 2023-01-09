package domain.preference.type

@JvmInline
value class StringPreference(override val value: String) : PreferenceValue {
    override fun toSave(): String = "$value,type=string"
    override fun toString(): String = toSave()
}