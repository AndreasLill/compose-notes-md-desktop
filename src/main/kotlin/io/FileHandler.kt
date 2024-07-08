package io

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.deleteExisting

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

    /**
     * Create a file from path and increment retry with number if already exists.
     */
    suspend fun createFile(path: Path): Path? = withContext(Dispatchers.IO) {
        val defaultName = "Untitled"
        var count = 0
        while (true) {
            try {
                val fileName = if (count > 0) "$defaultName ($count).md" else "$defaultName.md"
                val file = Files.createFile(Paths.get(path.toString(), "/", fileName))
                println("Created file: ${file.fileName}")
                return@withContext file
            } catch (ex: FileAlreadyExistsException) {
                count++
                println("Create file already exists: $ex")
            } catch (ex: IOException) {
                println("Error creating file: $ex")
                break
            }
        }
        return@withContext null
    }

    /**
     * Create a folder from path and increment retry with number if already exists.
     */
    suspend fun createFolder(path: Path): Path? = withContext(Dispatchers.IO) {
        val defaultName = "Folder"
        var count = 0
        while (true) {
            try {
                val fileName = if (count > 0) "$defaultName ($count)" else defaultName
                val folder = Files.createDirectory(Paths.get(path.toString(), "/", fileName))
                println("Created folder: ${folder.fileName}")
                return@withContext folder
            } catch (ex: FileAlreadyExistsException) {
                count++
                println("Create folder already exists: $ex")
            } catch (ex: IOException) {
                println("Error creating folder: $ex")
                break
            }
        }
        return@withContext null
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

    suspend fun deleteFolder(path: Path): Boolean = withContext(Dispatchers.IO) {
        try {
            val paths = Files.walk(path).sorted(Comparator.reverseOrder())
            paths.forEach { file ->
                try {
                    file.deleteExisting()
                } catch (ex: IOException) {
                    println("Error deleting file: $ex")
                }
            }
            return@withContext true
        } catch (ex: IOException) {
            println("Error deleting folder: $ex")
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