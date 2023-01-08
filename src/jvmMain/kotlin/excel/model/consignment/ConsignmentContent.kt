package excel.model.consignment

import excel.model.Product

data class ConsignmentContent(
    val serialNumber: String,
    val date: String,
    val sender: String,
    val products: List<Product>
) {
    override fun toString(): String {
        return "Consignment(\nserialNumber=$serialNumber, date=$date, sender=$sender, products=${products.joinToString("\n", "\n", "\n")})"
    }
}