package experimental.module

class CoreModule(
    fileSystemModule: FileSystemModule,
    excelModule: ExcelModule,
    xmlModule: XmlModule,
    consignmentModule: ConsignmentModule
) {
    private var fileSystemModule: FileSystemModule? = fileSystemModule
    private var excelModule: ExcelModule? = excelModule
    private var xmlModule: XmlModule? = xmlModule
    private var consignmentModule: ConsignmentModule? = consignmentModule


}