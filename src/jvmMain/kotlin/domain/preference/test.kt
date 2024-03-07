package domain.preference

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import data.excel.data.Handler
import data.excel.data.Reader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

private fun visualizeExcelFile() {
    val scope = CoroutineScope(Dispatchers.Default)
    val dirPath = "C:\\Users\\arsen\\OneDrive\\Desktop\\test\\"
    val fileName = "BLRWBL002-4810958900002-0000038443126.xlsx"

    val excelFiles = Reader.read(dirPath + fileName)
    val consignment = Handler.handle(excelFiles).first()
    val outputText = StringBuilder()

    consignment.sheet.rowIterator().forEach { row ->
        row.forEach { cell ->
            if (cell.stringCellValue?.isNotBlank() == true) {
                val cellContent = "${cell.toString().replace("\n", "")}_${cell.rowIndex},${cell.columnIndex}|"
                outputText.append(cellContent)
                print(cellContent)
            }
        }
        outputText.appendLine()
        println()
    }
    val outputStream = FileOutputStream("C:\\Users\\arsen\\OneDrive\\Desktop\\test\\output.txt")
    outputStream.use { out ->
        out.channel.write(ByteBuffer.wrap(outputText.toString().toByteArray()))
    }
}

private fun checkThemePreference() {
    val scope = CoroutineScope(Dispatchers.Default)
    val preferences = Preferences(scope)
    CoroutineScope(Dispatchers.Default).launch {
        preferences.savePreference("consignmentPath", Preferences.debugConsignmentPath)
        preferences.savePreference("resultPath", Preferences.debugResultPath)
        preferences.savePreference("theme", "dark")

        delay(1000)
        preferences.savePreference("theme", "light")
        delay(1000)
        preferences.savePreference("theme", "dark")
        delay(1000)
        preferences.savePreference("theme", "light")
        delay(1000)
        preferences.savePreference("theme", "dark")
    }
    runBlocking {
        preferences.getUnsafe<String>("theme")
            .onEach { println(it) }
            .collect()
    }
}