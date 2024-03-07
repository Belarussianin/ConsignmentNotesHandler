package data.excel.test

import data.excel.data.ConsignmentNotesHandler
import data.excel.data.Handler
import data.excel.data.Reader
import data.excel.data.Writer
import data.excel.pmap
import org.apache.poi.ss.usermodel.Sheet
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

const val resources = "C:\\Users\\arsen\\OneDrive\\Desktop\\октябрь"//"./src/jvmMain/resources"

@OptIn(ExperimentalTime::class)
fun main() {
    ConsignmentNotesHandler.handle(
        pathToConsignments = resources,
        pathToResult = "C:\\Users\\arsen\\OneDrive\\Desktop\\resultS"
    )
}

fun sheetToRowList(sheet: Sheet): List<String> {
    val rowList = mutableListOf<String>()
    for (x in 0 until sheet.physicalNumberOfRows) {
        val row = sheet.getRow(x)
        val stringRow = StringBuilder()
        for (y in 0 until row.physicalNumberOfCells) {
            val cell = row.getCell(y)
            if (cell.toString().isNotBlank()) {
                stringRow.append(cell.toString())
            }
        }
        rowList.add(stringRow.toString())
    }
    return rowList
}

@OptIn(ExperimentalTime::class)
fun testRun() {
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