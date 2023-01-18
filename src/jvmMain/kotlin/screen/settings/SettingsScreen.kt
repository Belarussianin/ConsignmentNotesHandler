package screen.settings

import AppScreen
import StringResources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import domain.preference.Preference
import domain.preference.Preferences
import domain.preference.type.BooleanPreference
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    preferences: Preferences,
    stringResources: StringResources,
    navigateTo: (AppScreen) -> Unit
) {
    val scope = rememberCoroutineScope()

    val isDarkTheme = preferences.get<BooleanPreference>("isDarkTheme")
        .map { it?.value?.value }
        .collectAsState(null)

    val isEnglishLanguage = preferences.get<BooleanPreference>("isEnglishLanguage")
        .map { it?.value?.value }
        .collectAsState(null)

    when {
        isDarkTheme == null -> LaunchedEffect(Unit) {
            preferences.save(
                Preference(
                    "isDarkTheme",
                    BooleanPreference(false)
                )
            )
        }

        isEnglishLanguage == null -> LaunchedEffect(Unit) {
            preferences.save(
                Preference(
                    "isEnglishLanguage",
                    BooleanPreference(false)
                )
            )
        }

        else -> {
            SettingsContent(
                settingsList = listOf(
                    Setting(
                        stringResources.themeSettingText,
                        stringResources.themeEnabledSettingText,
                        stringResources.themeDisabledSettingText,
                        isDarkTheme.value ?: false
                    ) to {
                        scope.launch {
                            preferences.save(Preference("isDarkTheme", BooleanPreference(it)))
                        }
                    },
                    Setting(
                        stringResources.languageSettingText,
                        "English",
                        "Русский",
                        isEnglishLanguage.value ?: false
                    ) to {
                        scope.launch {
                            preferences.save(Preference("isEnglishLanguage", BooleanPreference(it)))
                        }
                    },
                )
            )
        }
    }
}