package data.excel.data

import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

object ConsignmentNotesHandler {

    const val resources = "./src/jvmMain/resources"
    const val defaultConsignmentsDirectory = "$resources/consignments/"
    const val defaultResultDirectory = "./"

    @OptIn(ExperimentalTime::class)
    fun handle(
        pathToConsignments: String? = null,
        pathToResult: String? = null,
        resultFileName: String = "result"
    ): Triple<Duration, Duration, Duration> {
        //TODO
        // check paths and name
        val (excelFiles, readDuration) = measureTimedValue {
            Reader.read(pathToConsignments?.plus("\\") ?: defaultConsignmentsDirectory)
        }
        val (consignments, handleDuration) = measureTimedValue {
            Handler.handle(excelFiles)
        }
        val (_, writeDuration) = measureTimedValue {
            Writer.write(
                consignments,
                resultPathname = "${pathToResult?.plus("\\") ?: defaultResultDirectory}$resultFileName.xlsx"
            )
        }
        return Triple(readDuration, handleDuration, writeDuration)
    }

}