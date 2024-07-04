package io

import kotlinx.coroutines.*
import java.io.File

object FileHandler {
    fun readFile(file: File?): String = runBlocking {
        withContext(Dispatchers.IO) {
            if (file == null)
                return@withContext ""

            println("Read file ${file.name}")
            file.bufferedReader().use { reader ->
                val str = reader.readText()
                return@withContext str
            }
        }
    }

    fun saveFile(file: File?, data: String) = runBlocking {
        withContext(Dispatchers.IO) {
            file?.let { file ->
                file.bufferedWriter().use { writer ->
                    writer.write(data)
                }
                println("Saved file ${file.name}")
            }
        }
    }

    fun createFile(workspace: String, name: String) = runBlocking {
        withContext(Dispatchers.IO) {
            File("$workspace/$name.md").createNewFile()
            println("Created file $name.md")
        }
    }
}