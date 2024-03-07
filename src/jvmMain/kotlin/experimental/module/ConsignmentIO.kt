package experimental.module

import experimental.consignment.Consignment
import java.io.File

interface ConsignmentIO {
    fun read(file: File): Consignment

    fun write(
        consignments: List<Consignment>,
        resultPathname: String = "result.xlsx"
    ): File
}