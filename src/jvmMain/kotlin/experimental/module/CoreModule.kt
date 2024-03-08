package experimental.module

import experimental.module.FileSystemModule.allConsignmentFiles
import java.io.File
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

object CoreModule {
    val excelModule: ExcelModule = ExcelModule()
    val xmlModule: XmlModule = XmlModule()
    val consignmentModule: ConsignmentModule = ConsignmentModule(excelModule, xmlModule)

    const val resources = "./src/jvmMain/resources"
    const val defaultConsignmentsDirectory = "consignments"
    const val defaultResultDirectory = "results"
    const val defaultDirectory = "./"

    @OptIn(ExperimentalTime::class)
    fun convert(
        pathToConsignments: String? = null,
        pathToResult: String? = null,
        resultFileName: String = "result"
    ): Pair<File, Triple<Duration, Duration, Duration>> {
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
            resultFile
        }
        println(resultFile)
        return resultFile to Triple(readDuration, handleDuration, writeDuration)
    }
}