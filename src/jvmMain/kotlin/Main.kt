import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
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
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import data.excel.data.ConsignmentNotesHandler
import domain.preference.Preference
import domain.preference.Preferences
import domain.preference.type.StringPreference
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView
import kotlin.time.Duration

//TODO
// different options to browse file on different platforms
// https://genuinecoder.com/how-to-open-file-explorer-in-java/#:~:text=The%20easiest%20way%20to%20do,given%20file%20is%20a%20directory.&text=From%20Java%209%20onwards%2C%20Desktop,desktop%20module.

private fun openDirectory(directory: File) {
    Runtime.getRuntime().exec("explorer ${directory.absolutePath}")
}

@Composable
fun FileChooserDialog(
    title: String,
    currentFile: String? = null,
    onResult: (result: File?) -> Unit
) {
    val fileChooser = JFileChooser(FileSystemView.getFileSystemView()).apply {
        currentDirectory = File(currentFile ?: ".")
        dialogTitle = title
        fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
        isAcceptAllFileFilterUsed = true
    }
    if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        val file = fileChooser.selectedFile
        onResult(file)
    } else {
        onResult(null)
    }
}

@Composable
fun FileChooserCard(
    title: String,
    currentState: String? = null,
    isEnabled: Boolean = true,
    onFileChanged: (File?) -> Unit = {}
) {
    var state by remember(currentState) { mutableStateOf(currentState) }
    var isFileChooserOpen by remember { mutableStateOf(false) }

    if (isFileChooserOpen) {
        FileChooserDialog(
            title = title,
            currentFile = state,
            onResult = {
                isFileChooserOpen = false
                onFileChanged(it)
                state = it?.absolutePath
                println("Result $it")
            }
        )
    }
    Column(horizontalAlignment = Alignment.End) {
        OutlinedTextField(state ?: "", {}, readOnly = true)
        Button(
            onClick = {
                isFileChooserOpen = true
            },
            enabled = isEnabled
        ) {
            Text(title)
        }
    }
}

@Composable
@Preview
fun App() {
    val preferences = remember { Preferences() }
    var isConvertButtonEnabled by remember { mutableStateOf(true) }
    var convertButtonText by remember { mutableStateOf("Конвертировать") }
    var lastConvertDuration by remember { mutableStateOf<Triple<Duration, Duration, Duration>?>(null) }
    var isConvertInProcess by remember { mutableStateOf(false) }

    val consignmentDirectory = preferences.get("consignmentPath")
        .map { it?.value?.value?.toString() ?: ConsignmentNotesHandler.defaultConsignmentsDirectory }
        .collectAsState(ConsignmentNotesHandler.defaultConsignmentsDirectory)
    val resultDirectory = preferences.get("resultPath")
        .map { it?.value?.value?.toString() ?: ConsignmentNotesHandler.defaultResultDirectory }
        .collectAsState(ConsignmentNotesHandler.defaultResultDirectory)

    val scope = rememberCoroutineScope()

    LaunchedEffect(isConvertInProcess) {
        if (isConvertInProcess) {
            lastConvertDuration = ConsignmentNotesHandler.handle(
                pathToConsignments = consignmentDirectory.value,
                pathToResult = resultDirectory.value
            )
            isConvertButtonEnabled = true
            convertButtonText = "Конвертировать"
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
                    title = "Choose consignment file/directory",
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
                    title = "Choose result directory",
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
                        convertButtonText = "Конвертация"
                        isConvertInProcess = true
                    },
                    enabled = isConvertButtonEnabled
                ) {
                    Text(convertButtonText)
                }
                lastConvertDuration?.let {
                    val (readDuration, handleDuration, writeDuration) = it
                    val wholeDuration = it.first + it.second + it.third
                    Text("Read took ${readDuration.inWholeSeconds} s, ${readDuration.inWholeMilliseconds % 1000} ms.")
                    Text("Handle took ${handleDuration.inWholeSeconds} s, ${handleDuration.inWholeMilliseconds % 1000} ms.")
                    Text("Write took ${writeDuration.inWholeSeconds} s, ${writeDuration.inWholeMilliseconds % 1000} ms.")
                    Text("App took ${wholeDuration.inWholeSeconds} s, ${wholeDuration.inWholeMilliseconds % 1000} ms.")
                    Button(
                        onClick = {
                            openDirectory(File(resultDirectory.value))
                        }
                    ) {
                        Text("Open result directory")
                    }
                }
            }
        }
    }
}

fun main() = application {
    Window(
        title = "Consignment Notes Handler",
        state = WindowState(
            placement = WindowPlacement.Floating,
            position = WindowPosition(Alignment.Center)
        ),
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}
