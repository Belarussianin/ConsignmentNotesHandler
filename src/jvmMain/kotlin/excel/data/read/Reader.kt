package excel.data.read

import excel.isExcelFile
import excel.model.excel.ExcelFile
import excel.pmap
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