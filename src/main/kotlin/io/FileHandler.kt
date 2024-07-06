package io

import kotlinx.coroutines.*
import java.io.File

object FileHandler {
    suspend fun readFile(file: File?): String = withContext(Dispatchers.IO) {
        if (file == null)
            return@withContext ""

        println("Read file ${file.name}")

        file.bufferedReader().use { reader ->
            val str = reader.readText()
            return@withContext str
        }
    }

    suspend fun saveFile(file: File?, data: String) = withContext(Dispatchers.IO) {
        file?.let { file ->
            file.bufferedWriter().use { writer ->
                writer.write(data)
            }
            println("Saved file ${file.name}")
        }
    }

    suspend fun createFile(workspace: String, name: String): File? = withContext(Dispatchers.IO) {
        val success = File("$workspace/$name.md").createNewFile()

        if (success) {
            println("Created file $name.md")
            return@withContext File("$workspace/$name.md")
        }

        println("Could not create file $name.md - already exists")
        return@withContext null
    }

    suspend fun deleteFile(file: File): Boolean = withContext(Dispatchers.IO) {
        println("Deleted file ${file.name}")
        return@withContext file.delete()
    }
}