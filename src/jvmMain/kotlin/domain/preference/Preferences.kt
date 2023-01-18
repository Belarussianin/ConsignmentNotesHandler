package domain.preference

import domain.preference.type.PreferenceValue
import domain.preference.type.StringPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class Preferences(
    val savePath: String = "B:\\IdeaProjects\\ConsignmentNotesHandler\\src\\jvmMain\\resources\\saveFile.txt"
) {

    private val saveFile = File(savePath).apply { createNewFile() }

    private val preferences: ConcurrentHashMap<String, MutableStateFlow<Preference<PreferenceValue>?>> = ConcurrentHashMap()

    suspend fun loadPreference(name: String): Preference<PreferenceValue>? {
        val result = withContext(Dispatchers.IO) {
            saveFile.useLines { lines ->
                lines.find { Preference.parseNameFromLine(it) == name }?.let { preferenceLine ->
                    Preference.parseFromLine(preferenceLine)
                }
            }
        }
        return result
    }

    suspend fun loadAllPreferences(): List<Preference<PreferenceValue>> {
        val result = withContext(Dispatchers.IO) {
            saveFile.useLines { lines -> lines.map { Preference.parseFromLine(it) }.toList() }
        }
        return result
    }

    suspend fun save(preference: Preference<PreferenceValue>) {
        preferences[preference.name]?.emit(preference)
        withContext(Dispatchers.IO) {
            val preferenceToSave = preference.toString().plus("\n")
            var isSaved = false
            val oldLines = saveFile.readLines()
            oldLines.forEachIndexed { index, line ->
                if (isSaved) return@forEachIndexed
                val parsedName = Preference.parseNameFromLine(line)
                if (parsedName == preference.name) {
                    val firstLines = oldLines.take(index).joinToString("\n", "", "") + if (index == 0) "" else "\n"
                    saveFile.writeText(firstLines)
                    saveFile.appendText(preferenceToSave)
                    val lastLines = oldLines.takeLast(oldLines.size - (index + 1))
                        .joinToString("\n", "", "") + if (oldLines.size - (index + 1) == 0) "" else "\n"
                    saveFile.appendText(lastLines)
                    isSaved = true
                }
            }
            if (!isSaved) {
                saveFile.appendText(preferenceToSave)
            }
        }
    }

    fun <T: PreferenceValue> get(name: String): Flow<Preference<T>?> {
        return preferences.getOrPut(name) { MutableStateFlow(runBlocking { loadPreference(name) }) } as Flow<Preference<T>?>
    }
}

fun main() {
    val preferences = Preferences("B:\\IdeaProjects\\ConsignmentNotesHandler\\src\\jvmMain\\resources\\saveFile.txt")
    CoroutineScope(Dispatchers.Default).launch {
        preferences.save(
            Preference(
                "consignmentPath",
                StringPreference("B:\\IdeaProjects\\ConsignmentNotesHandler\\src\\jvmMain\\resources\\consignments")
            )
        )
        preferences.save(
            Preference(
                "resultPath",
                StringPreference("B:\\IdeaProjects\\ConsignmentNotesHandler\\src\\jvmMain\\resources")
            )
        )
        preferences.save(Preference("theme", StringPreference("dark")))
        delay(1000)
        preferences.save(Preference("theme", StringPreference("light")))
        delay(1000)
        preferences.save(Preference("theme", StringPreference("dark")))
        delay(1000)
        preferences.save(Preference("theme", StringPreference("light")))
        delay(1000)
        preferences.save(Preference("theme", StringPreference("dark")))
    }
    runBlocking {
        preferences.get<StringPreference>("theme")
            .onEach { println(it) }
            .collect()
    }
}