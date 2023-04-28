package data.excel.data

import data.excel.model.consignment.Consignment
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Writer {

    fun write(consignments: List<Consignment>, resultPathname: String = "result.xlsx") {
        var rowCounter = 0
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Sheet1")

        val headerRow = sheet.createRow(rowCounter)

        Columns.values().forEach {
            val cell = headerRow.createCell(it.ordinal).center(workbook).bold(workbook)
            cell.setCellValue(it.value)
        }

        sheet.apply {
            setColumnWidth(Columns.SenderWithIdAndDate.ordinal, 25 * 256)
            setColumnWidth(Columns.ProductName.ordinal, 35 * 256)
            setColumnWidth(Columns.CountEmpty.ordinal, 15 * 256)
            setColumnWidth(Columns.PriceEmpty.ordinal, 15 * 256)
            setColumnWidth(Columns.Price.ordinal, 15 * 256)
            setColumnWidth(Columns.Count.ordinal, 15 * 256)
        }

        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        val result = consignments.sortedBy {
            LocalDate.parse(it.content.date, dateTimeFormatter)
        }

        result.forEach { consignment ->
            consignment.content.products.forEach { product ->
                val row = createRow(sheet, ++rowCounter)
                product.apply {
                    consignment.content.apply {
                        val trimmedSender = sender.takeWhile { it != ',' }.trim()
                        row.getCell(Columns.SenderWithIdAndDate.ordinal)
                            .regular(workbook)
                            .setStringCellValue("$trimmedSender ЭТТН $serialNumber от $date г.")
                    }
                    row.getCell(Columns.ProductName.ordinal)
                        .regular(workbook)
                        .setStringCellValue(name)
                    row.getCell(Columns.Price.ordinal)
                        .regular(workbook)
                        .center(workbook)
                        .setNumericCellValue(price.toDouble())
                    row.getCell(Columns.Count.ordinal)
                        .regular(workbook)
                        .center(workbook)
                        .setNumericCellValue(count.toDouble())
                }
            }
        }
        //autoSizeColumns(sheet)
        saveSheet(workbook, resultPathname)
    }

    private fun Cell.setStringCellValue(value: String) {
        setCellValue(value)
        setCellFormat(CellFormat.STRING)
    }

    private fun Cell.setNumericCellValue(value: Double) {
        //TODO fix -> (num as text error)
        val formattedValue = String.format("%.2f", value)
        setCellValue(formattedValue)
        setCellFormat(CellFormat.NUMERIC)
    }

    private enum class CellFormat(val fmt: Short) {
        NUMERIC(2),
        STRING(0x31)
    }

    /**
     * cellStyle: 2 - numeric
     * cellStyle: 0x31 - text
     */
    private fun Cell.setCellFormat(cellFormat: CellFormat): Cell {
        this.cellStyle.dataFormat = cellFormat.fmt
        return this
    }

    private fun saveSheet(book: XSSFWorkbook, pathname: String) {
        val outputStream = FileOutputStream(pathname)
        outputStream.use {
            book.write(it)
        }
    }

    private fun createRow(sheet: XSSFSheet, index: Int): Row {
        val row = sheet.createRow(index)
        Columns.values().forEach {
            row.createCell(it.ordinal)
        }
        return row
    }

    private fun Cell.regular(book: XSSFWorkbook) = apply {
        cellStyle = book.createCellStyle().apply {
            alignment = HorizontalAlignment.LEFT
            verticalAlignment = VerticalAlignment.TOP
            setFont(regularFont(book))
            wrapText = true
        }
    }

    private fun Cell.bold(book: XSSFWorkbook) = apply {
        cellStyle.setFont(boldFont(book))
    }

    private fun Cell.center(book: XSSFWorkbook) = apply {
        cellStyle = book.createCellStyle().apply {
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            wrapText = true
        }
    }

    private fun autoSizeColumns(sheet: XSSFSheet) {
        Columns.values().forEach {
            sheet.autoSizeColumn(it.ordinal)
        }
    }

    private fun regularFont(book: XSSFWorkbook): Font = book.createFont().also { font ->
        font.fontHeight = (10 * 20).toShort()
        font.fontName = "Times New Roman"
        font.bold = false
    }

    private fun boldFont(book: XSSFWorkbook): Font = book.createFont().also { font ->
        font.fontHeight = (10 * 20).toShort()
        font.fontName = "Times New Roman"
        font.bold = true
    }

    private enum class Columns(val value: String) {
        SenderWithIdAndDate("Поставщик товара, документ, его номер и дата"),
        ProductName("Наименование, вид (сорт, артикул) товара"),
        CountEmpty("количество"),
        PriceEmpty("стоимость (руб.)"),
        Price("цена"),
        Count("количество")
    }
}