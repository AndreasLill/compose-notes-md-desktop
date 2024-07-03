package io

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

object FileHandler {
    fun readFile(file: File?) = runBlocking {
        if (file == null)
            return@runBlocking ""

        withContext(Dispatchers.IO) {
            println("read file")
            file.bufferedReader().use {
                return@withContext it.readText()
            }
        }
    }

    fun saveFile(file: File?, data: String) = runBlocking {
        file?.let {
            withContext(Dispatchers.IO) {
                it.bufferedWriter().use {
                    it.write(data)
                }
                println("saved file")
            }
        }
    }
}