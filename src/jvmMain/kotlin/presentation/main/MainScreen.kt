package presentation.main

import AppScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import consignments.StringResources
import experimental.map
import experimental.module.ConsignmentModule
import experimental.module.CoreModule
import experimental.module.ExcelModule
import experimental.module.XmlModule
import experimental.openDirectory
import experimental.preference.Preferences
import experimental.preference.StandardPreferences
import kotlinx.coroutines.launch
import presentation.common.FileChooserCard
import java.io.File
import kotlin.time.Duration

@Composable
fun MainScreen(
    preferences: Preferences,
    stringResources: StringResources,
    navigateTo: (AppScreen) -> Unit
) {
    val scope = rememberCoroutineScope()

    var isConvertButtonEnabled by remember { mutableStateOf(true) }
    var convertButtonText by remember { mutableStateOf(stringResources.convertButtonText) }
    var lastConvertDuration by remember { mutableStateOf<Triple<Duration, Duration, Duration>?>(null) }
    var isConvertInProcess by remember { mutableStateOf(false) }

    val consignmentDirectory by preferences.getStandardOnlyValue(StandardPreferences.ConsignmentPath.default)
        .map(scope) { path -> File(path).also { if (!it.exists()) it.mkdir() }.absolutePath }
        .collectAsState()
    val resultDirectory by preferences.getStandardOnlyValue(StandardPreferences.ResultPath.default)
        .map(scope) { path -> File(path).also { if (!it.exists()) it.mkdir() }.absolutePath }
        .collectAsState()

    LaunchedEffect(isConvertInProcess) {
        if (isConvertInProcess) {
            lastConvertDuration = CoreModule(ConsignmentModule(ExcelModule(), XmlModule())).convert(
                pathToConsignments = consignmentDirectory,
                pathToResult = resultDirectory
            )
            isConvertButtonEnabled = true
            convertButtonText = stringResources.convertButtonText
            isConvertInProcess = false
        }
    }

    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                FileChooserCard(
                    title = stringResources.chooseConsignmentButtonText,
                    currentState = consignmentDirectory,
                    isEnabled = !isConvertInProcess
                ) {
                    it?.let {
                        scope.launch {
                            preferences.saveStandardPreference(StandardPreferences.ConsignmentPath(it.absolutePath))
                        }
                    }
                }
                FileChooserCard(
                    title = stringResources.chooseResultDirectoryButtonText,
                    currentState = resultDirectory,
                    isEnabled = !isConvertInProcess
                ) {
                    it?.let {
                        scope.launch {
                            preferences.saveStandardPreference(StandardPreferences.ResultPath(it.absolutePath))
                        }
                    }
                }
                Button(
                    onClick = {
                        isConvertButtonEnabled = false
                        convertButtonText = stringResources.convertInProgressButtonText
                        isConvertInProcess = true
                    },
                    enabled = isConvertButtonEnabled
                ) {
                    Text(convertButtonText)
                }
                lastConvertDuration?.let {
                    val (readDuration, handleDuration, writeDuration) = it
                    val wholeDuration = it.first + it.second + it.third
                    Text("${stringResources.readTookText} ${readDuration.inWholeSeconds} s, ${readDuration.inWholeMilliseconds % 1000} ms.")
                    Text("${stringResources.handleTookText} ${handleDuration.inWholeSeconds} s, ${handleDuration.inWholeMilliseconds % 1000} ms.")
                    Text("${stringResources.writeTookText} ${writeDuration.inWholeSeconds} s, ${writeDuration.inWholeMilliseconds % 1000} ms.")
                    Text("${stringResources.allTookText} ${wholeDuration.inWholeSeconds} s, ${wholeDuration.inWholeMilliseconds % 1000} ms.")
                    Button(
                        onClick = { openDirectory(File(resultDirectory)) }
                    ) {
                        Text(stringResources.openResultDirectoryButtonText)
                    }
                }
            }
        }
    }
}