package presentation.settings

data class Setting(
    val name: String,
    val enabledName: String,
    val disabledName: String = enabledName,
    val isEnabled: Boolean = false
)