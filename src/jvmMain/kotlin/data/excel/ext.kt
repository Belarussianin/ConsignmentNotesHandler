package data.excel

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

fun File.isOldExcelFile(): Boolean = name.endsWith(".xls")

fun File.isNewExcelFile(): Boolean = name.endsWith(".xlsx")

fun File.isExcelFile(): Boolean = isOldExcelFile() || isNewExcelFile()

fun File.isConsignmentExcelFile(): Boolean = isExcelFile() && nameWithoutExtension.contains("BLRWBL")

/** TODO UNSTABLE
//inline fun <T, R> Sequence<T>.pmap(
//    numThreads: Int = Runtime.getRuntime().availableProcessors() - 2,
//    exec: ExecutorService = Executors.newFixedThreadPool(numThreads),
//    crossinline transform: (T) -> R
//): List<R> {
//
//    // default size is just an inlined version of kotlin.collections.collectionSizeOrDefault
//    val defaultSize = if (this is Collection<*>) this.size else 10
//    val destination = Collections.synchronizedList(ArrayList<R>(defaultSize))
//
//    for (item in this) {
//        exec.submit { destination.add(transform(item)) }
//    }
//
//    exec.shutdown()
//    exec.awaitTermination(1, TimeUnit.DAYS)
//
//    return ArrayList<R>(destination)
//}
//
//inline fun <T, R> Iterable<T>.pmap(
//    numThreads: Int = Runtime.getRuntime().availableProcessors() - 2,
//    exec: ExecutorService = Executors.newFixedThreadPool(numThreads),
//    crossinline transform: (T) -> R
//): List<R> {
//
//    // default size is just an inlined version of kotlin.collections.collectionSizeOrDefault
//    val defaultSize = if (this is Collection<*>) this.size else 10
//    val destination = Collections.synchronizedList(ArrayList<R>(defaultSize))
//
//    for (item in this) {
//        exec.submit { destination.add(transform(item)) }
//    }
//
//    exec.shutdown()
//    exec.awaitTermination(1, TimeUnit.DAYS)
//
//    return ArrayList<R>(destination)
//}
**/

fun <T, M> StateFlow<T>.map(
    coroutineScope: CoroutineScope,
    mapper: (value: T) -> M
): StateFlow<M> = map { mapper(it) }.stateIn(
    coroutineScope,
    SharingStarted.Eagerly,
    mapper(value)
)