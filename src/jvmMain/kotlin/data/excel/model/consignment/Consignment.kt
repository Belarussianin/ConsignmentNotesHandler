package data.excel.model.consignment

import data.excel.data.Handler
import data.excel.model.Product
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFSheet


class Consignment(
    val sheet: Sheet
) {
    val content: ConsignmentContent = extractContent(sheet)

    override fun toString(): String {
        return "Consignment(\n" +
                "serialNumber=${content.serialNumber}, date=${content.date}, sender=${content.sender}, products=${
                    content.products.joinToString("\n", "\n", "\n")
                })"
    }

    companion object {
        //TODO not working yet
        @JvmStatic
        fun shiftRow(sheet: Sheet, workbook: Workbook, topRowNum: Int) {
            when (sheet) {
                is XSSFSheet -> {
                    sheet.shiftRows(topRowNum, sheet.lastRowNum, 1)
                }

                is HSSFSheet -> {
                    insertRow(workbook as HSSFWorkbook, sheet, topRowNum)
                }
            }
        }
        //TODO not working yet
        @JvmStatic
        private fun insertRow(
            workbook: HSSFWorkbook,
            worksheet: HSSFSheet,
            topRowNum: Int
        ) {
            // Get the source / new row
            var newRow = worksheet.getRow(topRowNum)
            val sourceRow = worksheet.getRow(topRowNum)

            // If the row exist in destination, push down all rows by 1 else create a new row
            if (newRow != null) {
                worksheet.shiftRows(topRowNum, worksheet.lastRowNum, 1)
            } else {
                newRow = worksheet.createRow(topRowNum)
            }

            // Loop through source columns to add to new row
            for (i in 0 until sourceRow.lastCellNum) {
                // Grab a copy of the old/new cell
                val oldCell = sourceRow.getCell(i)
                var newCell = newRow.createCell(i)

                // If the old cell is null jump to next cell
                if (oldCell == null) {
                    newCell = null
                    continue
                }

                // Copy style from old cell and apply to new cell
                val newCellStyle = workbook.createCellStyle()
                newCellStyle.cloneStyleFrom(oldCell.cellStyle)
                newCell.setCellStyle(newCellStyle)

                // If there is a cell comment, copy
                if (oldCell.cellComment != null) {
                    newCell.setCellComment(oldCell.cellComment)
                }

                // If there is a cell hyperlink, copy
                if (oldCell.hyperlink != null) {
                    newCell.setHyperlink(oldCell.hyperlink)
                }

                // Set the cell data type
                if (oldCell.cellType != CellType.FORMULA)
                    newCell.cellType = oldCell.cellType
                when (oldCell.cellType) {
                    CellType.BLANK -> newCell.setCellValue(oldCell.stringCellValue)
                    CellType.BOOLEAN -> newCell.setCellValue(oldCell.booleanCellValue)
                    CellType.ERROR -> newCell.setCellErrorValue(oldCell.errorCellValue)
                    CellType.FORMULA -> newCell.cellFormula = oldCell.cellFormula
                    CellType.NUMERIC -> newCell.setCellValue(oldCell.numericCellValue)
                    CellType.STRING -> newCell.setCellValue(oldCell.richStringCellValue)
                    else -> {}
                }
            }

            // If there are any merged regions in the source row, copy to new row
            for (i in 0 until worksheet.numMergedRegions) {
                val cellRangeAddress = worksheet.getMergedRegion(i)
                if (cellRangeAddress.firstRow == sourceRow.rowNum) {
                    val newCellRangeAddress = CellRangeAddress(
                        newRow.rowNum,
                        newRow.rowNum +
                                (cellRangeAddress.lastRow - cellRangeAddress.firstRow),
                        cellRangeAddress.firstColumn,
                        cellRangeAddress.lastColumn
                    )
                    worksheet.addMergedRegion(newCellRangeAddress)
                }
            }
        }

        @JvmStatic
        fun extractContent(sheet: Sheet): ConsignmentContent {
            val cellList: List<Cell> = Handler.sheetToCellList(sheet)
            val serialNumber: String
            val data: String
            val sender: String
            val productNumberColumn: Int
            val rowsWithProducts: List<Int>
            val noteIndex: Int
            cellList.asSequence()
                .also { it -> serialNumber = cellList[it.indexOfFirst { it.toString() == "Серия" } + 1].toString() }
                .also { it -> data = cellList[it.indexOfFirst { it.toString() == "Дата" } + 1].toString() }
                .also { it ->
                    sender = cellList[it.indexOfLast { it.toString() == "Грузоотправитель" } + 1].toString()
                }
                .also { it ->
                    productNumberColumn =
                        cellList[it.indexOfFirst {
                            it.toString().contains("ТОВАРНЫЙ РАЗДЕЛ", true)
                        } + 1].columnIndex
                }
                .also { it ->
                    rowsWithProducts =
                        it.filter { it.columnIndex == productNumberColumn && it.toString().toIntOrNull() != null }
                            .map { it.rowIndex }.toList()
                }
                .also { it -> noteIndex = it.indexOfFirst { it.toString() == "Примечание" } }

            val productNameColumn = cellList[noteIndex + 1].columnIndex
            val productCountColumn = cellList[noteIndex + 3].columnIndex
            //val productPriceColumn = cellList[noteIndex + 4].columnIndex
            val productPriceColumn = cellList[noteIndex + 8].columnIndex //NDS included

            val products = rowsWithProducts.asSequence()
                .map { sheet.getRow(it) }
                .map {
                    val name = it.getCell(productNameColumn).toString()
                    val count = it.getCell(productCountColumn).toString()
                    //val price = it.getCell(productPriceColumn).toString()
                    val price = it.getCell(productPriceColumn).toString().toFloat() / count.toFloat()  //NDS included
                    Product(name, count, price.toString())
                }
                .toList()

            return ConsignmentContent(serialNumber, data, sender, products)
        }
    }
}
