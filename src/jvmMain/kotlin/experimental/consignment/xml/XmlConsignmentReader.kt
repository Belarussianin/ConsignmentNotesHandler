package experimental.consignment.xml

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import experimental.file.XmlFile

class XmlConsignmentReader(
    private val xmlFile: XmlFile
) : XmlMapper() {
    constructor(pathname: String) : this(XmlFile(pathname))
    constructor(dirPath: String, fileName: String) : this(XmlFile(dirPath, fileName))

    /**
     * Blocking (IO) call
     */
    fun read(): XmlConsignment = readValue(xmlFile.file, XmlConsignment::class.java)
        ?: throw XmlConsignmentReaderException("consignment is null")
}

class XmlConsignmentReaderException(message: String) : RuntimeException() {
    override val message = "XmlConsignmentReaderException: $message"
}

@JacksonXmlRootElement(localName = "DeliveryNote")
@JsonIgnoreProperties(ignoreUnknown = true)
data class DeliveryDetails(
    @JacksonXmlProperty(localName = "DeliveryNoteID")
    val id: String,
    @JacksonXmlProperty(localName = "DeliveryNoteDate")
    val date: String,
    @JacksonXmlProperty(localName = "Shipper")
    val shipper: Shipper,
    @JacksonXmlProperty(localName = "DespatchAdviceLogisticUnitLineItem")
    val products: Products,
)

@JacksonXmlRootElement(localName = "Shipper")
@JsonIgnoreProperties(ignoreUnknown = true)
data class Shipper(
    @JacksonXmlProperty(localName = "Name")
    val name: String
)

@JacksonXmlRootElement(localName = "DespatchAdviceLogisticUnitLineItem")
@JsonIgnoreProperties(ignoreUnknown = true)
data class Products(
    @JacksonXmlProperty(localName = "LineItem")
    @JacksonXmlElementWrapper(useWrapping = false)
    val value: List<XmlProduct>
)