package domain.preference.type

@JvmInline
value class IntPreference(override val value: Int) : PreferenceValue {
    override fun toSave(): String = "$value,type=int"
    override fun toString(): String = toSave()
}