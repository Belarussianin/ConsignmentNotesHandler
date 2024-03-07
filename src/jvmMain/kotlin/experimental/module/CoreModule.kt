package experimental.module

import experimental.module.FileSystemModule.allConsignmentFiles
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class CoreModule(
//    val fileSystemModule: FileSystemModule,
//    val excelModule: ExcelModule,
//    val xmlModule: XmlModule,
    val consignmentModule: ConsignmentModule
) {
    companion object {
        const val resources = "./src/jvmMain/resources"
        const val defaultConsignmentsDirectory = "consignments"
        const val defaultResultDirectory = "results"
        const val defaultDirectory = "./"
    }

    @OptIn(ExperimentalTime::class)
    fun convert(
        pathToConsignments: String? = null,
        pathToResult: String? = null,
        resultFileName: String = "result"
    ): Triple<Duration, Duration, Duration> {
        //TODO
        // check paths and name
        val path = (pathToConsignments?.plus("\\") ?: defaultConsignmentsDirectory)

        val (files, readDuration) = measureTimedValue {
            val files = allConsignmentFiles(path)
            files
        }
        val (consignments, handleDuration) = measureTimedValue {
            val consignments = files.map {
                consignmentModule.read(it)
            }
            consignments
        }
        val (resultFile, writeDuration) = measureTimedValue {
            val resultFile = consignmentModule.write(
                consignments,
                resultPathname = "${pathToResult?.plus("\\") ?: defaultResultDirectory}$resultFileName.xlsx"
            )
        }
        return Triple(readDuration, handleDuration, writeDuration)
    }
}