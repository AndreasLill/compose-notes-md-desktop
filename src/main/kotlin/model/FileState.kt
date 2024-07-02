package model

import kotlinx.coroutines.*
import java.io.File

class FileState {
    fun readFile(file: File?) = runBlocking {
        if (file == null)
            return@runBlocking ""

        withContext(Dispatchers.IO) {
            return@withContext file.bufferedReader().readText()
        }
    }

    fun saveFile(file: File?, data: String) = runBlocking {
        file?.let {
            withContext(Dispatchers.IO) {
                val writer = it.bufferedWriter()
                writer.write(data)
                writer.flush()
                println("saved file")
            }
        }
    }
}