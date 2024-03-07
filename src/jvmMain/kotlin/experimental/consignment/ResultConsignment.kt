package experimental.consignment

import data.excel.model.Product

data class ResultConsignment(
    val serialNumber: String,
    val date: String,
    val sender: String,
    val products: List<ResultProduct>
) {
    constructor(xmlConsignment: XmlConsignment) : this(
        serialNumber = xmlConsignment.details.id,
        date = xmlConsignment.details.date,
        sender = xmlConsignment.details.shipper.name,
        products = xmlConsignment.details.products.value
    )


        override fun toString(): String {
            return "Consignment(\nserialNumber=$serialNumber, date=$date, sender=$sender, products=${products.joinToString("\n", "\n", "\n")})"
        }
}
