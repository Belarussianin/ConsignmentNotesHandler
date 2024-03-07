package experimental.file

import experimental.xml.isConsignmentXmlFile
import java.io.File

class XmlFile(
    val file: File
) {
    constructor(pathName: String) : this(File(pathName))
    constructor(dirPath: String, fileName: String) : this("$dirPath\\$fileName")

    init {
        when {
            !file.isFile -> throw XmlFileException("Not a file")
            !file.isConsignmentXmlFile() -> throw XmlFileException("Not Consignment XML file")
        }
    }
}

class XmlFileException(message: String) : RuntimeException() {
    override val message = "XmlFileException: $message"
}