package domain.preference

import data.excel.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class Preferences(
    val scope: CoroutineScope,
    val savePath: String = debugPreferencesSavePath
) {
    companion object {
        const val debugPreferencesSavePath: String =
            "B:\\IdeaProjects\\ConsignmentNotesHandler\\src\\jvmMain\\resources\\saveFile.txt"
        const val releasePreferencesSavePath: String = "\\saveFile.txt"

        const val debugConsignmentPath: String =
            "B:\\IdeaProjects\\ConsignmentNotesHandler\\src\\jvmMain\\resources\\consignments"
        const val debugResultPath: String = "B:\\IdeaProjects\\ConsignmentNotesHandler\\src\\jvmMain\\resources"
    }

    private val saveFile = File(savePath).apply { createNewFile() }

    private val preferences: ConcurrentHashMap<String, MutableStateFlow<Preference<out Any>?>> =
        ConcurrentHashMap()

    suspend fun loadPreference(name: String): Preference<out Any>? {
        val result = withContext(Dispatchers.IO) {
            saveFile.useLines { lines ->
                lines.find { Preference.parseNameFromLine(it) == name }?.let { preferenceLine ->
                    Preference.parseFromLine(preferenceLine)
                }
            }
        }
        return result
    }

    suspend fun loadAllPreferences(): List<Preference<out Any>> {
        val result = withContext(Dispatchers.IO) {
            saveFile.useLines { lines -> lines.map { Preference.parseFromLine(it) }.toList() }
        }
        return result
    }

    suspend fun <T : Any> savePreference(name: String, value: T) {
        savePreference(Preference(name, PreferenceValue(value)))
    }

    suspend fun saveStandardPreference(standardPreference: StandardPreferences<out Any>) {
        savePreference(standardPreference.toPreference())
    }

    suspend fun savePreference(preference: Preference<out Any>) {
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

    fun <T : Any> getUnsafe(name: String): StateFlow<Preference<T>?> {
        return preferences.getOrPut(name) { MutableStateFlow(runBlocking(Dispatchers.IO) { loadPreference(name) }) }
            .asStateFlow() as StateFlow<Preference<T>?>
    }

    fun <T : Any> getStandard(standardPreference: StandardPreferences<T>): StateFlow<Preference<T>> {
        return preferences.getOrPut(standardPreference.name) {
            MutableStateFlow(
                runBlocking(Dispatchers.IO) {
                    loadPreference(standardPreference.name) ?: saveStandardPreference(standardPreference)
                    loadPreference(standardPreference.name)
                }
            )
        }.asStateFlow() as StateFlow<Preference<T>>
    }

    fun <T : Any> getStandardOnlyValue(standardPreference: StandardPreferences<T>): StateFlow<T> {
        return preferences.getOrPut(standardPreference.name) {
            MutableStateFlow(
                runBlocking(Dispatchers.IO) {
                    loadPreference(standardPreference.name) ?: saveStandardPreference(standardPreference)
                    loadPreference(standardPreference.name)
                }
            )
        }.map(scope) { it!!.value.value } as StateFlow<T>
    }
}