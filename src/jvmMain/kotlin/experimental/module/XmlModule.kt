package experimental.module

import experimental.consignment.Consignment
import experimental.consignment.xml.XmlConsignmentReader
import experimental.file.XmlFile
import java.io.File

class XmlModule : ConsignmentIO {

    override fun read(file: File): Consignment {
        return XmlConsignmentReader(XmlFile(file)).read()
    }

    override fun write(consignments: List<Consignment>, resultPathname: String): File {
        TODO("Not yet implemented")
    }
}