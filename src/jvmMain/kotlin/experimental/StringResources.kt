package consignments

data class StringResources(
    val appName: String,
    val chooseConsignmentButtonText: String,
    val chooseResultDirectoryButtonText: String,
    val convertButtonText: String,
    val convertInProgressButtonText: String,
    val openResultDirectoryButtonText: String,
    val themeSettingText: String,
    val themeEnabledSettingText: String,
    val themeDisabledSettingText: String,
    val languageSettingText: String,
    val readTookText: String,
    val handleTookText: String,
    val writeTookText: String,
    val allTookText: String,
    val settingsText: String
)

val englishResources: StringResources = StringResources(
    "Consignment Notes Handler",
    "Choose consignment file/directory",
    "Choose result directory",
    "Convert",
    "Convert in progress",
    "Open result directory",
    "Theme",
    "Dark",
    "Light",
    "Language",
    "Read took",
    "Handle took",
    "Write took",
    "All took",
    "Settings"
)

val russianResources: StringResources = StringResources(
    "Обработчик накладных",
    "Выберите файл/директорию с накладными",
    "Выберите директорию для результата",
    "Конвертировать",
    "Конвертация",
    "Открыть директорию результата",
    "Тема",
    "Темная",
    "Светлая",
    "Язык",
    "Чтение заняло",
    "Обработка заняла",
    "Запись заняла",
    "Всё заняло",
    "Настройки"
)