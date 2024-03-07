package experimental.file.excel

import experimental.excel.isConsignmentExcelFile
import experimental.excel.isOldExcelFile
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

class ExcelFile(
    val file: File
) {
    constructor(pathName: String) : this(File(pathName))
    constructor(dirPath: String, fileName: String) : this("$dirPath\\$fileName")

    init {
        when {
            !file.isConsignmentExcelFile() -> throw ExcelFileException("not Consignment Excel file")
        }
    }

    val type: ExcelFileType = if (file.isOldExcelFile()) OldExcelFile else NewExcelFile
    val sheet: Sheet = excelFileSheet()

    private fun excelFileSheet(): Sheet {
        return when (type) {
            is OldExcelFile -> oldExcelFileSheet()
            is NewExcelFile -> newExcelFileSheet()
        }
    }

    private fun newExcelFileSheet(): Sheet {
        return XSSFWorkbook(file).use { workBook: XSSFWorkbook ->
            workBook.getSheetAt(0)
        }
    }

    private fun oldExcelFileSheet(): Sheet {
        return file.inputStream().use { fileInputStream ->
            HSSFWorkbook(fileInputStream).use { workBook: HSSFWorkbook ->
                workBook.getSheetAt(0)
            }
        }
    }
}

class ExcelFileException(message: String) : RuntimeException() {
    override val message: String = "ExcelFileException: $message"
}