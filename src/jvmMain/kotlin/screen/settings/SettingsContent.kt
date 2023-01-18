package screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SettingsContent(
    settingsList: List<Pair<Setting, (Boolean) -> Unit>>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        items(settingsList) { (setting, onSettingChange) ->
            SettingSwitch(setting, setting.isEnabled, onSettingChange)
        }
    }
}

@Composable
fun SettingSwitch(
    setting: Setting,
    isSettingEnabled: Boolean,
    onSettingChange: ((Boolean) -> Unit) = {}
) {
    setting.apply {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "$name: ${if (isSettingEnabled) enabledName else disabledName}",
                color = Color.Black
            )
            Switch(
                checked = isSettingEnabled,
                onCheckedChange = onSettingChange
            )
        }
    }
}