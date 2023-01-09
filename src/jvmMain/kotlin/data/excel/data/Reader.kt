package data.excel.data

import data.excel.isExcelFile
import data.excel.model.excel.ExcelFile
import data.excel.pmap
import java.io.File

object Reader {

    fun read(pathname: String): List<ExcelFile> {
        return File(pathname)
            .walk()
            .filter { it.isExcelFile() }
            .asIterable()
            .pmap { ExcelFile(it) }
    }

}