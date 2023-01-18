package data.excel

import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

//TODO
// different options to browse file on different platforms
// https://genuinecoder.com/how-to-open-file-explorer-in-java/#:~:text=The%20easiest%20way%20to%20do,given%20file%20is%20a%20directory.&text=From%20Java%209%20onwards%2C%20Desktop,desktop%20module.

fun openDirectory(directory: File) {
    Runtime.getRuntime().exec("explorer ${directory.absolutePath}")
}

fun File.isOldExcelFile(): Boolean = name.endsWith(".xls")

fun File.isNewExcelFile(): Boolean = name.endsWith(".xlsx")

fun File.isExcelFile(): Boolean = isOldExcelFile() || isNewExcelFile()

inline fun <T, R> Iterable<T>.pmap(
    numThreads: Int = Runtime.getRuntime().availableProcessors() - 2,
    exec: ExecutorService = Executors.newFixedThreadPool(numThreads),
    crossinline transform: (T) -> R
): List<R> {

    // default size is just an inlined version of kotlin.collections.collectionSizeOrDefault
    val defaultSize = if (this is Collection<*>) this.size else 10
    val destination = Collections.synchronizedList(ArrayList<R>(defaultSize))

    for (item in this) {
        exec.submit { destination.add(transform(item)) }
    }

    exec.shutdown()
    exec.awaitTermination(1, TimeUnit.DAYS)

    return ArrayList<R>(destination)
}