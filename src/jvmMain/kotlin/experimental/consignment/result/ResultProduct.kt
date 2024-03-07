package experimental.consignment.result

import experimental.consignment.Product
import experimental.consignment.xml.XmlProduct
import java.math.RoundingMode
import kotlin.math.roundToInt

/**
 * @property [name] Наименование товара
 * @property [quantity] Количество
 * @property [amount] Цена с НДС (руб. коп.) = [XmlProduct.amount] (Стоимость с НДС) \ [XmlProduct.quantity] (Количество)
 */
data class ResultProduct(
    override val name: String,
    override val quantity: String,
    override val amount: String
) : Product {
    constructor(product: Product) : this(
        product.name,
        calculateQuantity(product.quantity),
        calculateAmount(product.quantity, product.amount)
    )
}

/**
 * @return [ResultProduct.quantity] Количество
 */
private fun calculateQuantity(quantity: String): String {
    return quantity.toDouble().roundToInt().toString()
}

/**
 * @return [ResultProduct.amount] Цена с НДС (руб. коп.)
 */
private fun calculateAmount(quantity: String, amount: String): String {
    val amountRaw = amount.toFloat() / quantity.toFloat()
    return scaleAmount(amountRaw)
}

private fun scaleAmount(amountRaw: Float): String =
    amountRaw.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toString()