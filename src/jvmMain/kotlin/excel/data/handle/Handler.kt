package excel.data.handle

import excel.model.consignment.Consignment
import excel.model.excel.ExcelFile
import excel.model.excel.NewExcelFile
import excel.model.excel.OldExcelFile
import excel.pmap
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

object Handler {

    fun handle(excelFiles: List<ExcelFile>): List<Consignment> {
        return excelFiles.pmap {
            excelFileToConsignment(it)
        }
    }

    fun sheetToCellList(sheet: Sheet): List<Cell> {
        val cellList = mutableListOf<Cell>()
        for (x in 0 until sheet.physicalNumberOfRows) {
            val row = sheet.getRow(x)
            for (y in 0 until row.physicalNumberOfCells) {
                val cell = row.getCell(y)
                if (cell.toString().isNotBlank()) {
                    cellList.add(cell)
                }
            }
        }
        return cellList
    }

    private fun oldExcelFileToConsignment(excelFile: ExcelFile): Consignment {
        return excelFile.file.inputStream().use { fileInputStream ->
            HSSFWorkbook(fileInputStream).use { workBook: HSSFWorkbook ->
                val sheet = workBook.getSheetAt(0)
                Consignment(sheet)
            }
        }
    }

    private fun newExcelFileToConsignment(excelFile: ExcelFile): Consignment {
        return XSSFWorkbook(excelFile.file).use { workBook: XSSFWorkbook ->
            val sheet = workBook.getSheetAt(0)
            Consignment(sheet)
        }
    }

    fun excelFileToConsignment(excelFile: ExcelFile): Consignment {
        return when (excelFile.type) {
            is OldExcelFile -> oldExcelFileToConsignment(excelFile)
            is NewExcelFile -> newExcelFileToConsignment(excelFile)
        }
    }
}