package application.io

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.io.IOException
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory

object FileHandler {
    private const val DEFAULT_FILE_NAME = "Untitled"
    private const val DEFAULT_FOLDER_NAME = "Untitled"

    enum class WalkBehavior {
        FoldersFirst,
        FilesFirst,
    }

    /**
     * Read all file contents.
     */
    suspend fun readFile(path: Path): String? = withContext(Dispatchers.IO) {
        try {
            return@withContext Files.readString(path)
        } catch (ex: IOException) {
            println("Error reading file: $ex")
            return@withContext null
        }
    }

    /**
     * Save all file contents.
     */
    suspend fun saveFile(path: Path, data: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Files.writeString(path, data)
            return@withContext true
        } catch (ex: IOException) {
            println("Error saving file: $ex")
            return@withContext false
        }
    }

    /**
     * Create a file or folder from path and increment retry with number if already exists.
     */
    suspend fun create(path: Path, folder: Boolean): Path? = withContext(Dispatchers.IO) {
        var count = 0
        while (true) {
            try {
                if (folder) {
                    val name = if (count > 0) "$DEFAULT_FOLDER_NAME ($count)" else DEFAULT_FOLDER_NAME
                    return@withContext Files.createDirectory(Paths.get(path.toString(), "/", name))
                } else {
                    val name = if (count > 0) "$DEFAULT_FILE_NAME ($count).md" else "$DEFAULT_FILE_NAME.md"
                    return@withContext Files.createFile(Paths.get(path.toString(), "/", name))
                }
            } catch (ex: FileAlreadyExistsException) {
                count++
            } catch (ex: IOException) {
                println("Error creating file: $ex")
                break
            }
        }
        return@withContext null
    }

    /**
     * Delete a file by moving it to OS recycle bin.
     */
    suspend fun delete(path: Path): Boolean = withContext(Dispatchers.IO) {
        try {
            val desktop = Desktop.getDesktop()
            if (path.isDirectory()) {
                val paths = Files.walk(path).sorted(Comparator.reverseOrder())
                paths.forEach {
                    desktop.moveToTrash(it.toFile())
                }
            } else {
                desktop.moveToTrash(path.toFile())
            }
            return@withContext true
        } catch (ex: IOException) {
            println("Error deleting: $ex")
            return@withContext false
        }
    }

    /**
     * Rename a file or folder.
     */
    suspend fun rename(path: Path, toName: String): Path? = withContext(Dispatchers.IO) {
        try {
            return@withContext Files.move(path, Paths.get(path.parent.toString(), toName))
        } catch (ex: IOException) {
            println("Error renaming file: $ex")
            return@withContext null
        }
    }

    /**
     * Move a file or folder.
     */
    suspend fun move(path: Path, toPath: Path): Path? = withContext(Dispatchers.IO) {
        try {
            val targetDir = if (toPath.isDirectory()) toPath.toString() else toPath.parent.toString()
            return@withContext Files.move(path, Paths.get(targetDir, path.fileName.toString()))
        } catch (ex: IOException) {
            println("Error moving file: $ex")
            return@withContext null
        }
    }

    suspend fun isValidWorkspace(path: Path): Boolean = withContext(Dispatchers.IO) {
        var valid = true
        Files.list(path).forEach {
            if (!Files.isReadable(it) || !Files.isWritable(it)) {
                valid = false
                return@forEach
            }
        }
        return@withContext valid
    }

    /**
     * Get a list of files and folder paths by walking a path using depth first traversal recursively.
     * WalkBehavior decides if the traversal order should be files or folders first.
     * All files and folders are sorted alphabetically.
     */
    suspend fun walkPathDepthFirst(path: Path, behavior: WalkBehavior): List<Path> {
        try {
            val result = mutableListOf<Path>()

            val paths = withContext(Dispatchers.IO) {
                Files.list(path)
            }.toList()

            val folders = paths.filter { it.isDirectory() }.sorted()
            val files = paths.filter { !it.isDirectory() }.sorted()

            if (behavior == WalkBehavior.FoldersFirst) {
                folders.forEach {
                    result.add(it)
                    result.addAll(walkPathDepthFirst(it, behavior))
                }
                result.addAll(files)
            }
            if (behavior == WalkBehavior.FilesFirst) {
                result.addAll(files)
                folders.forEach {
                    result.add(it)
                    result.addAll(walkPathDepthFirst(it, behavior))
                }
            }

            return result
        } catch (ex: IOException) {
            return emptyList()
        }
    }
}