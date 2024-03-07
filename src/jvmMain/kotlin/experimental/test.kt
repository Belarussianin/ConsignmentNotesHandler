package experimental

import experimental.consignment.xml.XmlConsignmentReader
import experimental.module.ConsignmentModule
import experimental.module.CoreModule
import experimental.module.ExcelModule
import experimental.module.XmlModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun main() {
    expTest()
}

private val dirPath = "C:\\Users\\arsen\\OneDrive\\Desktop\\test\\XML_EXPERIMENT\\"
private val fileName = "BLRWBL_002-4810958900002-0000124996215.xml"

private fun expTest() {
    val consignmentModule = ConsignmentModule(
        excelModule = ExcelModule(),
        xmlModule = XmlModule()
    )
    val coreModule = CoreModule(
        consignmentModule = consignmentModule
    )
    val (readTime, processingTime, writeTime) = coreModule.convert(dirPath, dirPath, "SomeWeirdShit")

    println("Read took ${readTime.inWholeSeconds} s, ${readTime.inWholeMilliseconds % 1000} ms.")
    println("Handle took ${processingTime.inWholeSeconds} s, ${processingTime.inWholeMilliseconds % 1000} ms.")
    println("Write took ${writeTime.inWholeSeconds} s, ${writeTime.inWholeMilliseconds % 1000} ms.")
    val wholeDuration = readTime + processingTime + writeTime
    println("App took ${wholeDuration.inWholeSeconds} s, ${wholeDuration.inWholeMilliseconds % 1000} ms.")
}

private fun xmlTest() {
    val xmlConsignmentReader = XmlConsignmentReader(dirPath, fileName)

//    val consignment = xmlConsignmentReader.readOnce()
//    println(consignment.details)

    val scope = CoroutineScope(Dispatchers.IO)
    scope.launch {
        //xmlConsignmentReader.close()
    }
    xmlConsignmentReader.read()

    while (true) {
    }
}