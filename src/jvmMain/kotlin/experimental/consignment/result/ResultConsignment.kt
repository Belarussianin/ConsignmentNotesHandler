package experimental.consignment.result

import experimental.consignment.Consignment
import experimental.consignment.Product
import experimental.consignment.excel.ExcelConsignment
import experimental.consignment.xml.XmlConsignment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class ResultConsignment(
    val serialNumber: String,
    val date: String,
    val sender: String,
    val products: List<ResultProduct>
) : Consignment {
    constructor(consignment: Consignment) : this(
        serialNumber = calculateSerialNumber(consignment),
        date = calculateDate(consignment),
        sender = calculateSender(consignment),
        products = calculateProducts(consignment)
    )
}

private fun calculateSerialNumber(consignment: Consignment): String {
    return when (consignment) {
        is ExcelConsignment -> consignment.content.serialNumber
        is XmlConsignment -> consignment.content.id
        is ResultConsignment -> consignment.serialNumber
        else -> throw ResultConsignmentException("unknown consignment type")
    }
}

private fun calculateDate(consignment: Consignment): String {
    val (date, formatter) = when (consignment) {
        is ExcelConsignment -> consignment.content.date to DateTimeFormatter.ofPattern("dd.MM.yyyy")
        is XmlConsignment -> consignment.content.date to DateTimeFormatter.ofPattern("yyyyMMdd")
        is ResultConsignment -> consignment.date to DateTimeFormatter.ofPattern("dd.MM.yyyy")
        else -> throw ResultConsignmentException("unknown consignment type")
    }
    return DateTimeFormatter.ofPattern("dd.MM.yyyy").format(LocalDate.parse(date, formatter))
}

private fun calculateSender(consignment: Consignment): String {
    return when (consignment) {
        is ExcelConsignment -> consignment.content.sender
        is XmlConsignment -> consignment.content.shipper.name
        is ResultConsignment -> consignment.sender
        else -> throw ResultConsignmentException("unknown consignment type")
    }
}

private fun calculateProducts(products: List<Product>): List<ResultProduct> = products.map { ResultProduct(it) }
private fun calculateProducts(consignment: Consignment): List<ResultProduct> = when (consignment) {
    is ExcelConsignment -> calculateProducts(consignment.content.products)
    is XmlConsignment -> calculateProducts(consignment.content.products.value)
    is ResultConsignment -> consignment.products
    else -> throw ResultConsignmentException("unknown consignment type")
}

class ResultConsignmentException(message: String) : RuntimeException() {
    override val message = "ResultConsignmentException: $message"
}