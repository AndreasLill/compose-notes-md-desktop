package io

import kotlinx.coroutines.*
import java.io.File
import kotlin.io.path.Path

object FileHandler {
    suspend fun readFile(file: File?): String = withContext(Dispatchers.IO) {
        file?.let { file ->
            file.bufferedReader().use { reader ->
                val str = reader.readText()
                println("Read file ${file.name}")
                return@withContext str
            }
        }

        return@withContext ""
    }

    suspend fun saveFile(file: File?, data: String) = withContext(Dispatchers.IO) {
        file?.let { file ->
            file.bufferedWriter().use { writer ->
                writer.write(data)
            }
            println("Saved file ${file.name}")
        }
    }

    suspend fun createFile(workspace: File?, name: String): File? = withContext(Dispatchers.IO) {
        workspace?.let { workspace ->
            val success = File("$workspace/$name.md").createNewFile()

            if (success) {
                println("Created file $name.md")
                return@withContext File("$workspace/$name.md")
            }

            println("Could not create file $name.md - already exists")
            return@withContext null
        }
    }

    suspend fun deleteFile(file: File): Boolean = withContext(Dispatchers.IO) {
        println("Deleted file ${file.name}")
        return@withContext file.delete()
    }

    suspend fun renameFile(file: File, name: String): File = withContext(Dispatchers.IO) {
        val renamedFile = Path(file.parent, "$name.md").toFile()
        file.renameTo(renamedFile)
        return@withContext renamedFile
    }
}