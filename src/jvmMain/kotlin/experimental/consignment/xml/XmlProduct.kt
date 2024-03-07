package experimental.consignment.xml

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import experimental.consignment.Product

@JacksonXmlRootElement(localName = "LineItem")
@JsonIgnoreProperties(ignoreUnknown = true)
data class XmlProduct(
    /**
     * Наименование товара
     */
    @JacksonXmlProperty(localName = "LineItemName")
    override val name: String,
    /**
     * Количество
     */
    @JacksonXmlProperty(localName = "QuantityDespatched")
    override val quantity: String,
    /**
     * Стоимость с НДС (руб. коп.)
     */
    @JacksonXmlProperty(localName = "LineItemAmount")
    override val amount: String
) : Product