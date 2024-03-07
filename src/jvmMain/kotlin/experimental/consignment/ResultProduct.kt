package experimental.consignment

/**
 * @property [name] Наименование товара
 * @property [quantity] Количество
 * @property [amount] Цена с НДС (руб. коп.) = [XmlProduct.amount] (Стоимость с НДС) \ [XmlProduct.quantity] (Количество)
 */
data class ResultProduct(
    val name: String,
    val quantity: String,
    val amount: String
) {
    constructor(xmlProduct: XmlProduct) : this(
        name = xmlProduct.name,
        quantity = xmlProduct.quantity,
        amount = xmlProduct.amount
    )
}
