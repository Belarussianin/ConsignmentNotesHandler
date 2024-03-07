package experimental.module

import data.excel.isConsignmentExcelFile
import experimental.xml.isConsignmentXmlFile
import java.io.File
import java.util.TreeSet

object FileSystemModule {

    fun File.serialNumber() = nameWithoutExtension
        .replace("BLRWBL", "")
        .replace(".xml", "")
        .replace(".xlsx", "")
        .replace(".xls", "")
        .replace("-", "")
        .replace("_", "")

    fun allConsignmentFiles(pathName: String): List<File> {
        val excelFiles = allExcelFiles(pathName)
        val xmlFiles = allXmlFiles(pathName)
        val allFiles = xmlFiles.plus(excelFiles)
        return TreeSet(allFiles).distinctBy { it.serialNumber() }
    }

    fun allExcelFiles(pathName: String): List<File> {
        return File(pathName)
            .walk()
            .filter { it.isConsignmentExcelFile() }
            .toList()
    }

    fun allXmlFiles(pathName: String): List<File> {
        return File(pathName)
            .walk()
            .filter { it.isConsignmentXmlFile() }
            .toList()
    }
}