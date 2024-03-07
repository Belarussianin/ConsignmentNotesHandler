package experimental.xml

import java.io.File

fun File.isXmlFile(): Boolean = name.endsWith(".xml")