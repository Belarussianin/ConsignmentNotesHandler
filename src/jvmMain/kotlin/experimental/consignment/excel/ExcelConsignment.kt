package experimental.consignment.excel

import experimental.consignment.Consignment
import experimental.file.ExcelFile
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Sheet
import java.io.File

class ExcelConsignment(private val sheet: Sheet) : Consignment {
    constructor(excelFile: ExcelFile) : this(excelFile.sheet)
    constructor(file: File) : this(ExcelFile(file))

    val content: ExcelConsignmentContent = extractContent()

    private fun sheetToCellList(sheet: Sheet): List<Cell> {
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

    @Deprecated("Use XML Consignment Reader instead")
    private fun extractContent(): ExcelConsignmentContent {
        val cellList: List<Cell> = sheetToCellList(sheet)
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
        val productPriceColumn = cellList[noteIndex + 8].columnIndex //NDS included

        val products = rowsWithProducts.asSequence()
            .map { sheet.getRow(it) }
            .map {
                val name = it.getCell(productNameColumn).toString()
                val count = it.getCell(productCountColumn).toString()
                val amount = it.getCell(productPriceColumn).toString()  //NDS included
                ExcelProduct(name, count, amount)
            }
            .toList()

        return ExcelConsignmentContent(serialNumber, data, sender, products)
    }
}