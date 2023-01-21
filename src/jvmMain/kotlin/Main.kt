import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.onClick
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import domain.preference.Preferences
import domain.preference.StandardPreferences
import presentation.main.MainScreen
import presentation.settings.SettingsScreen

enum class AppScreen {
    Main, Settings
}

fun getPreferencesPath(isForDebug: Boolean = true) =
    if (isForDebug) Preferences.debugPreferencesSavePath else Preferences.releasePreferencesSavePath

@OptIn(ExperimentalFoundationApi::class)
fun main() = application {
    val scope = rememberCoroutineScope()
    val preferences = remember { Preferences(scope, getPreferencesPath(isForDebug = false)) }

    val isEnglish by preferences.getStandardOnlyValue(StandardPreferences.IsEnglishLanguage.default).collectAsState()
    val currentResources = if (isEnglish) englishResources else russianResources

    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.Main) }

    Window(
        title = currentResources.appName,
        state = WindowState(
            placement = WindowPlacement.Floating,
            position = WindowPosition(Alignment.Center)
        ),
        onCloseRequest = ::exitApplication
    ) {
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            Modifier.size(32.dp)
            Icon(
                Icons.Default.Settings,
                currentResources.settingsText,
                Modifier.onClick(enabled = true,
                    matcher = PointerMatcher.mouse(PointerButton.Primary), // add onClick for every required PointerButton
                    keyboardModifiers = { true }, // e.g { isCtrlPressed }; Remove it to ignore keyboardModifiers
                    onClick = {
                        currentScreen =
                            if (currentScreen == AppScreen.Main) AppScreen.Settings else AppScreen.Main
                    }
                )
            )
            when (currentScreen) {
                AppScreen.Main -> MainScreen(preferences, currentResources) { currentScreen = it }
                AppScreen.Settings -> SettingsScreen(preferences, currentResources) { currentScreen = it }
            }
        }
    }
}