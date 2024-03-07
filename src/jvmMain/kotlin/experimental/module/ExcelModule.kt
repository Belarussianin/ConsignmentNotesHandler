package experimental.module

import experimental.consignment.Consignment
import experimental.consignment.excel.ExcelConsignment
import experimental.consignment.result.ResultConsignment
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class ExcelModule : ConsignmentIO {

    override fun read(file: File): ExcelConsignment {
        return ExcelConsignment(file)
    }

    override fun write(consignments: List<Consignment>, resultPathname: String): File {
        val resultConsignments = consignments.map { ResultConsignment(it) }

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

        val result = resultConsignments.sortedBy {
            LocalDate.parse(it.date, dateTimeFormatter)
        }

        for (consignment in result) {
            for (product in consignment.products) {
                val row = createRow(sheet, ++rowCounter)
                val trimmedSender = consignment.sender.takeWhile { it != ',' }.trim()

                fun Row.setCell(cellNum: Int, value: String, isRegular: Boolean = true, isCentered: Boolean = false) {
                    getCell(cellNum).apply {
                        takeIf { isRegular }?.regular(workbook)
                        takeIf { isCentered }?.center(workbook)
                        setCellValue(value)
                    }
                }

                row.setCell(
                    cellNum = Columns.SenderWithIdAndDate.ordinal,
                    value = "$trimmedSender ЭТТН ${consignment.serialNumber} от ${consignment.date} г."
                )
                row.setCell(
                    cellNum = Columns.ProductName.ordinal,
                    value = product.name
                )
                row.setCell(
                    cellNum = Columns.Price.ordinal,
                    value = product.amount,
                    isCentered = true
                )
                row.setCell(
                    cellNum = Columns.Count.ordinal,
                    value = product.quantity.toDouble().roundToInt().toString(),
                    isCentered = true
                )
            }
        }
        saveSheet(workbook, resultPathname)
        return File(resultPathname)
    }
}

fun saveSheet(book: XSSFWorkbook, pathname: String) {
    val outputStream = FileOutputStream(pathname)
    outputStream.use {
        book.write(it)
    }
}

fun createRow(sheet: XSSFSheet, index: Int): Row {
    val row = sheet.createRow(index)
    Columns.values().forEach {
        row.createCell(it.ordinal)
    }
    return row
}

fun Cell.regular(book: XSSFWorkbook) = apply {
    cellStyle = book.createCellStyle().apply {
        alignment = HorizontalAlignment.LEFT
        verticalAlignment = VerticalAlignment.TOP
        setFont(regularFont(book))
        wrapText = true
    }
}

fun Cell.bold(book: XSSFWorkbook) = apply {
    cellStyle.setFont(boldFont(book))
}

fun Cell.center(book: XSSFWorkbook) = apply {
    cellStyle = book.createCellStyle().apply {
        alignment = HorizontalAlignment.CENTER
        verticalAlignment = VerticalAlignment.CENTER
        wrapText = true
    }
}

fun autoSizeColumns(sheet: XSSFSheet) {
    Columns.values().forEach {
        sheet.autoSizeColumn(it.ordinal)
    }
}

fun regularFont(book: XSSFWorkbook): Font = book.createFont().also { font ->
    font.fontHeight = (10 * 20).toShort()
    font.fontName = "Times New Roman"
    font.bold = false
}

fun boldFont(book: XSSFWorkbook): Font = book.createFont().also { font ->
    font.fontHeight = (10 * 20).toShort()
    font.fontName = "Times New Roman"
    font.bold = true
}

enum class Columns(val value: String) {
    SenderWithIdAndDate("Поставщик товара, документ, его номер и дата"),
    ProductName("Наименование, вид (сорт, артикул) товара"),
    CountEmpty(""/*"количество"*/),
    PriceEmpty(""/*"стоимость (руб.)"*/),
    Price("цена"),
    Count("количество")
}