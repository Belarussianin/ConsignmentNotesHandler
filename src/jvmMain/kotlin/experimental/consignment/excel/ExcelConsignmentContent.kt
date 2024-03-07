package experimental.consignment.excel

data class ExcelConsignmentContent(
    val serialNumber: String,
    val date: String,
    val sender: String,
    val products: List<ExcelProduct>
)