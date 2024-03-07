package experimental.module

import data.excel.isConsignmentExcelFile
import experimental.consignment.Consignment
import experimental.xml.isConsignmentXmlFile
import java.io.File

class ConsignmentModule(
    val excelModule: ExcelModule,
    val xmlModule: XmlModule
) : ConsignmentIO {

    override fun read(file: File): Consignment {
        return when {
            file.isConsignmentExcelFile() -> excelModule.read(file)
            file.isConsignmentXmlFile() -> xmlModule.read(file)
            else -> throw Exception("Unsupported file type")
        }
    }

    override fun write(consignments: List<Consignment>, resultPathname: String): File {
        return excelModule.write(consignments, resultPathname)
    }
}