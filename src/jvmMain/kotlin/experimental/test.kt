package experimental

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import experimental.consignment.XmlConsignmentReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File

fun main() {
    xmlTest()
}

//TODO  1) select only BLRWBL .xml files
//      2) opt reader for list of files
private fun xmlTest() {
    val dirPath = "C:\\Users\\arsen\\OneDrive\\Desktop\\test\\XML_EXPERIMENT\\"
    val fileName = "BLRWBL_002-4810958900002-0000124996215.xml"
    val xmlConsignmentReader = XmlConsignmentReader(dirPath, fileName)

//    val consignment = xmlConsignmentReader.readOnce()
//    println(consignment.details)

    val scope = CoroutineScope(Dispatchers.IO)
    scope.launch {
        //xmlConsignmentReader.close()
    }
    xmlConsignmentReader.read()
    xmlConsignmentReader.subscribe {
        println(it)
    }

    while (true) {
    }
}