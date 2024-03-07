package experimental.excel

import java.io.File

fun File.isOldExcelFile(): Boolean = name.endsWith(".xls")

fun File.isNewExcelFile(): Boolean = name.endsWith(".xlsx")

fun File.isExcelFile(): Boolean = isOldExcelFile() || isNewExcelFile()

fun File.isConsignmentExcelFile(): Boolean = isExcelFile() && nameWithoutExtension.contains("BLRWBL")