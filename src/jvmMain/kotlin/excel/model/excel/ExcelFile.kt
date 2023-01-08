package excel.model.excel

import excel.isOldExcelFile
import java.io.File

class ExcelFile(
    val file: File
) {
    val type: ExcelFileType = if (file.isOldExcelFile()) OldExcelFile else NewExcelFile
}