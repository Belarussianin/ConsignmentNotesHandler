import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.mouseClickable
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import domain.preference.Preferences
import domain.preference.type.BooleanPreference
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import screen.common.LoadingScreen
import screen.main.MainScreen
import screen.settings.SettingsScreen

enum class AppScreen {
    Main, Settings
}

@OptIn(ExperimentalFoundationApi::class)
fun main() = application {
    val preferences = remember { Preferences() }

    val currentResources = preferences.get<BooleanPreference>("isEnglishLanguage")
        .map { it?.value?.value ?: false }
        .map { if (it) englishResources else russianResources }
        .distinctUntilChanged()
        .collectAsState(null)
    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.Main) }

    Window(
        title = currentResources.value?.appName ?: "",
        state = WindowState(
            placement = WindowPlacement.Floating,
            position = WindowPosition(Alignment.Center)
        ),
        onCloseRequest = ::exitApplication
    ) {
        when (val resources = currentResources.value) {
            null -> LoadingScreen()
            else -> {
                Box(
                    contentAlignment = Alignment.TopEnd
                ) {
                    Icon(
                        Icons.Default.Settings,
                        resources.settingsText,
                        Modifier.size(32.dp).mouseClickable {
                            currentScreen =
                                if (currentScreen == AppScreen.Main) AppScreen.Settings else AppScreen.Main
                        }
                    )
                    when (currentScreen) {
                        AppScreen.Main -> MainScreen(preferences, resources) { currentScreen = it }
                        AppScreen.Settings -> SettingsScreen(preferences, resources) { currentScreen = it }
                    }
                }
            }
        }
    }
}
