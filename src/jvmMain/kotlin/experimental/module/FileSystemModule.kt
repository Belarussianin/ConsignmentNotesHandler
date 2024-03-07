package experimental.module

import java.io.File

interface FileSystemModule {
    fun create()
    fun read(file: File)
    fun write(file: File)
    fun delete(file: File)
}