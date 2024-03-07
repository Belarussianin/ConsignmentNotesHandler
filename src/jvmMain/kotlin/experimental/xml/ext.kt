package experimental.xml

import java.io.File

fun File.isXmlFile(): Boolean = name.endsWith(".xml")

fun File.isConsignmentXmlFile(): Boolean = isXmlFile() && nameWithoutExtension.contains("BLRWBL")