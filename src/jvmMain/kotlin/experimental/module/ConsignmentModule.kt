package experimental.module

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import experimental.consignment.XmlConsignment
import experimental.xml.XmlFile
import java.io.File

interface ConsignmentModule {

    fun xmlConsignment(xmlFile: XmlFile): XmlConsignment {
        return XmlMapper().readValue(xmlFile, XmlConsignment::class.java)
            ?: throw Exception("XmlConsignmentReaderException: consignment is null")
    }
}