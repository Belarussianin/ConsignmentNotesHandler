package data.excel.model.excel

import data.excel.isOldExcelFile
import domain.excel.ExcelFileType
import domain.excel.NewExcelFile
import domain.excel.OldExcelFile
import java.io.File

class ExcelFile(
    val file: File
) {
    val type: ExcelFileType = if (file.isOldExcelFile()) OldExcelFile else NewExcelFile
}