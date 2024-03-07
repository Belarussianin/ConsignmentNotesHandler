package experimental.consignment.xml

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import experimental.consignment.Consignment

@JacksonXmlRootElement(localName = "BLRWBL")
@JsonIgnoreProperties(ignoreUnknown = true)
data class XmlConsignment(
    @JacksonXmlProperty(localName = "DeliveryNote")
    val content: DeliveryDetails,
) : Consignment