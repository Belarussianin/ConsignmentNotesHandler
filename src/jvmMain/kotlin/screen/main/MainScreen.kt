package screen.main

import AppScreen
import StringResources
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
import data.excel.data.ConsignmentNotesHandler
import data.excel.openDirectory
import domain.preference.Preference
import domain.preference.Preferences
import domain.preference.type.StringPreference
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import screen.common.FileChooserCard
import java.io.File
import kotlin.time.Duration

@Composable
fun MainScreen(
    preferences: Preferences,
    stringResources: StringResources,
    navigateTo: (AppScreen) -> Unit
) {
    var isConvertButtonEnabled by remember { mutableStateOf(true) }
    var convertButtonText by remember { mutableStateOf(stringResources.convertButtonText) }
    var lastConvertDuration by remember { mutableStateOf<Triple<Duration, Duration, Duration>?>(null) }
    var isConvertInProcess by remember { mutableStateOf(false) }

    val consignmentDirectory = preferences.get<StringPreference>("consignmentPath")
        .map { it?.value?.value ?: ConsignmentNotesHandler.defaultConsignmentsDirectory }
        .collectAsState(ConsignmentNotesHandler.defaultConsignmentsDirectory)
    val resultDirectory = preferences.get<StringPreference>("resultPath")
        .map { it?.value?.value ?: ConsignmentNotesHandler.defaultResultDirectory }
        .collectAsState(ConsignmentNotesHandler.defaultResultDirectory)

    val scope = rememberCoroutineScope()

    LaunchedEffect(isConvertInProcess) {
        if (isConvertInProcess) {
            lastConvertDuration = ConsignmentNotesHandler.handle(
                pathToConsignments = consignmentDirectory.value,
                pathToResult = resultDirectory.value
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
                    currentState = consignmentDirectory.value,
                    isEnabled = !isConvertInProcess
                ) {
                    it?.let {
                        scope.launch {
                            preferences.save(Preference("consignmentPath", StringPreference(it.absolutePath)))
                        }
                    }
                }
                FileChooserCard(
                    title = stringResources.chooseResultDirectoryButtonText,
                    currentState = resultDirectory.value,
                    isEnabled = !isConvertInProcess
                ) {
                    it?.let {
                        scope.launch {
                            preferences.save(Preference("resultPath", StringPreference(it.absolutePath)))
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
                        onClick = {
                            openDirectory(File(resultDirectory.value))
                        }
                    ) {
                        Text(stringResources.openResultDirectoryButtonText)
                    }
                }
            }
        }
    }
}