package experimental

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.io.File

//TODO
// different options to browse file on different platforms
// https://genuinecoder.com/how-to-open-file-explorer-in-java/#:~:text=The%20easiest%20way%20to%20do,given%20file%20is%20a%20directory.&text=From%20Java%209%20onwards%2C%20Desktop,desktop%20module.

fun openDirectory(directory: File) {
    Runtime.getRuntime().exec("explorer ${directory.absolutePath}")
}

fun <T, M> StateFlow<T>.map(
    coroutineScope: CoroutineScope,
    mapper: (value: T) -> M
): StateFlow<M> = map { mapper(it) }.stateIn(
    coroutineScope,
    SharingStarted.Eagerly,
    mapper(value)
)