package io

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.nameWithoutExtension

object FileHandler {
    suspend fun readFile(path: Path): String? = withContext(Dispatchers.IO) {
        try {
            println("Read file: ${path.fileName}")
            return@withContext Files.readString(path)
        } catch (ex: IOException) {
            println("Error reading file: $ex")
            return@withContext null
        }
    }

    suspend fun saveFile(path: Path, data: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Files.writeString(path, data)
            println("Saved file: ${path.fileName}")
            return@withContext true
        } catch (ex: IOException) {
            println("Error saving file: $ex")
            return@withContext false
        }
    }

    suspend fun createFile(path: Path): Path? = withContext(Dispatchers.IO) {
        val defaultName = "Untitled"
        val count = Files.walk(path, 1).filter { it.nameWithoutExtension.startsWith(defaultName) }.count()
        val fileName = if (count > 0) "$defaultName ($count).md" else "$defaultName.md"
        try {
            return@withContext Files.createFile(Paths.get(path.toString(), "/", fileName))
        } catch (ex: IOException) {
            println("Error creating file: $ex")
            return@withContext null
        }
    }

    suspend fun deleteFile(path: Path): Boolean = withContext(Dispatchers.IO) {
        try {
            Files.delete(path)
            println("Deleted file: ${path.fileName}")
            return@withContext true
        } catch (ex: IOException) {
            println("Error deleting file: $ex")
            return@withContext false
        }
    }

    suspend fun renameFile(path: Path, fromName: String, toName: String): Path? = withContext(Dispatchers.IO) {
        try {
            return@withContext Files.move(Paths.get(path.toString(), fromName), Paths.get(path.toString(), toName))
        } catch (ex: IOException) {
            println("Error renaming file: $ex")
            return@withContext null
        }
    }
}