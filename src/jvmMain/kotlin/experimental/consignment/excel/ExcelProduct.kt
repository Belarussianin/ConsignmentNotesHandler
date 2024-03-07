package experimental.consignment.excel

import experimental.consignment.Product

data class ExcelProduct(
    override val name: String,
    override val quantity: String,
    override val amount: String
) : Product