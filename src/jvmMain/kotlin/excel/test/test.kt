package excel.test

import excel.data.handle.Handler
import excel.data.read.Reader
import excel.data.write.Writer
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

const val resources = "./src/jvmMain/resources"

@OptIn(ExperimentalTime::class)
fun main() {
    val (excelFiles, readDuration) = measureTimedValue {
        val packageWithConsignments = "$resources/consignments/"
        Reader.read(packageWithConsignments)
    }
    val (consignments, handleDuration) = measureTimedValue {
        Handler.handle(excelFiles)
    }
    val (_, writeDuration) = measureTimedValue {
        Writer.write(consignments)
    }
    println("Read took ${readDuration.inWholeSeconds} s, ${readDuration.inWholeMilliseconds % 1000} ms.")
    println("Handle took ${handleDuration.inWholeSeconds} s, ${handleDuration.inWholeMilliseconds % 1000} ms.")
    println("Write took ${writeDuration.inWholeSeconds} s, ${writeDuration.inWholeMilliseconds % 1000} ms.")
    val wholeDuration = readDuration + handleDuration + writeDuration
    println("App took ${wholeDuration.inWholeSeconds} s, ${wholeDuration.inWholeMilliseconds % 1000} ms.")
}