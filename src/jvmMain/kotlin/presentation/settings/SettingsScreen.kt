package presentation.settings

import AppScreen
import StringResources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import domain.preference.Preferences
import domain.preference.StandardPreferences
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    preferences: Preferences,
    stringResources: StringResources,
    navigateTo: (AppScreen) -> Unit
) {
    val scope = rememberCoroutineScope()

    val isDarkTheme by preferences.getStandardOnlyValue(StandardPreferences.IsDarkTheme.default).collectAsState()
    val isEnglishLanguage by preferences.getStandardOnlyValue(StandardPreferences.IsEnglishLanguage.default).collectAsState()

    SettingsContent(
        settingsList = listOf(
            Setting(
                stringResources.themeSettingText,
                stringResources.themeEnabledSettingText,
                stringResources.themeDisabledSettingText,
                isDarkTheme
            ) to {
                scope.launch {
                    preferences.saveStandardPreference(StandardPreferences.IsDarkTheme(it))
                }
            },
            Setting(
                stringResources.languageSettingText,
                "English",
                "Русский",
                isEnglishLanguage
            ) to {
                scope.launch {
                    preferences.saveStandardPreference(StandardPreferences.IsEnglishLanguage(it))
                }
            },
        )
    )
}