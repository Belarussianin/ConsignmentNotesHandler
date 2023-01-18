package data.excel.model.consignment

import data.excel.data.Handler
import data.excel.model.Product
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Sheet

class Consignment(
    sheet: Sheet
) {
    val content: ConsignmentContent

    init {
        content = extractContent(sheet)
    }

    override fun toString(): String {
        return "Consignment(\n" +
                "serialNumber=${content.serialNumber}, date=${content.date}, sender=${content.sender}, products=${
                    content.products.joinToString("\n", "\n", "\n")
                })"
    }

    private companion object {
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
            val productPriceColumn = cellList[noteIndex + 4].columnIndex

            val products = rowsWithProducts.asSequence()
                .map { sheet.getRow(it) }
                .map {
                    val name = it.getCell(productNameColumn).toString()
                    val count = it.getCell(productCountColumn).toString()
                    val price = it.getCell(productPriceColumn).toString()
                    Product(name, count, price)
                }
                .toList()

            return ConsignmentContent(serialNumber, data, sender, products)
        }
    }
}
