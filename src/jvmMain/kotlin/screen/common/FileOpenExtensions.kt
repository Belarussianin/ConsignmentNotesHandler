package screen.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView

//TODO
// different options to browse file on different platforms
// https://genuinecoder.com/how-to-open-file-explorer-in-java/#:~:text=The%20easiest%20way%20to%20do,given%20file%20is%20a%20directory.&text=From%20Java%209%20onwards%2C%20Desktop,desktop%20module.

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