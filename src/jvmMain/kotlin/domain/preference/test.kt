package domain.preference

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
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