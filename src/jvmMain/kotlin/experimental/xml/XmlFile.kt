package experimental.xml

import java.io.File

class XmlFile(
    pathname: String
) : File(pathname) {
    constructor(dirPath: String, fileName: String) : this("$dirPath\\$fileName")

    init {
        when {
            !isFile -> throw XmlFileException("Not a file")
            !isXmlFile() -> throw XmlFileException("Not XML file")
        }
    }
}

class XmlFileException(message: String) : RuntimeException() {
    override val message = "XmlFileException: $message"
}