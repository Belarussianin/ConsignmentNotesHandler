package experimental.consignment

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import experimental.xml.XmlFile

class XmlConsignmentReader(
    private val xmlFile: XmlFile
) : XmlMapper() {
    constructor(pathname: String) : this(XmlFile(pathname))
    constructor(dirPath: String, fileName: String) : this(XmlFile(dirPath, fileName))

//    private val job = SupervisorJob()
//    private val coroutinesScope: CoroutineScope = CoroutineScope(job + Dispatchers.IO)
//    private val products = MutableSharedFlow<Consignment>(replay = 1)

    fun xmlConsignment(xmlFile: XmlFile): XmlConsignment {
        return XmlMapper().readValue(xmlFile, XmlConsignment::class.java)
            ?: throw Exception("XmlConsignmentReaderException: consignment is null")
    }

    /**
     * Blocking call
     * Doesn't emit new value to the "products" SharedFlow
     */
    fun read(): XmlConsignment = readValue(xmlFile, XmlConsignment::class.java)
        ?: throw XmlConsignmentReaderException("consignment is null")

//    /**
//     * Launches a new coroutine without blocking the current thread and returns a reference to the coroutine as a Job.
//     * The coroutine is cancelled when the resulting job is cancelled.
//     *
//     * Emits new value to the "products" SharedFlow
//     */
//    fun readInBackground() {
//        job.ensureActive()
//        coroutinesScope.launch {
//            products.emit(read())
//        }
//    }
//
//    fun subscribe(action: suspend (Consignment) -> Unit) {
//        job.ensureActive()
//        products.onEach(action).launchIn(coroutinesScope)
//    }
//
//    fun close() {
//        job.cancel()
//    }
}

class XmlConsignmentReaderException(message: String) : RuntimeException() {
    override val message = "XmlConsignmentReaderException: $message"
}

@JacksonXmlRootElement(localName = "BLRWBL")
@JsonIgnoreProperties(ignoreUnknown = true)
data class XmlConsignment(
    @JacksonXmlProperty(localName = "DeliveryNote")
    val details: DeliveryDetails,
)

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

@JacksonXmlRootElement(localName = "LineItem")
@JsonIgnoreProperties(ignoreUnknown = true)
data class XmlProduct(
    /**
     * Наименование товара
     */
    @JacksonXmlProperty(localName = "LineItemName")
    val name: String,
    /**
     * Количество
     */
    @JacksonXmlProperty(localName = "QuantityDespatched")
    val quantity: String,
    /**
     * Стоимость с НДС (руб. коп.)
     */
    @JacksonXmlProperty(localName = "LineItemAmount")
    val amount: String
)