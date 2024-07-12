package workspace.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import application.model.ApplicationState
import io.FileHandler
import java.awt.Desktop
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory

class WorkspaceViewModel(private val appState: ApplicationState) {
    val directory = mutableStateListOf<Path>()
    val openFolders = mutableStateListOf<Path>()
    var selectedItem by mutableStateOf<Path?>(null)

    fun selectItem(path: Path) {
        if (path.isDirectory() && !openFolders.contains(path) && selectedItem == path) {
            openFolders.add(path)
        } else if (path.isDirectory() && openFolders.contains(path) && selectedItem == path) {
            openFolders.removeIf { it.toString().contains(path.toString()) }
        } else if (!path.isDirectory()) {
            appState.file = path
        }
        selectedItem = path
    }

    fun openInExplorer(path: Path) {
        val dirPath = if (path.isDirectory()) path else path.parent
        Desktop.getDesktop().open(dirPath.toFile())
    }

    suspend fun createFile(path: Path) {
        val dirPath = if (path.isDirectory()) path else path.parent
        FileHandler.createFile(dirPath)?.let {
            updateDirectory()
            selectedItem = it
            appState.file = it
        }
    }

    suspend fun createFolder(path: Path) {
        val dirPath = if (path.isDirectory()) path else path.parent
        FileHandler.createFolder(dirPath)?.let {
            updateDirectory()
            selectedItem = it
            openFolders.add(it)
        }
    }

    suspend fun deletePath(path: Path) {
        FileHandler.delete(path)
        updateDirectory()
    }

    suspend fun renamePath(path: Path, toName: String) {
        FileHandler.rename(path, toName)?.let {
            if (!it.isDirectory()) {
                appState.file = it
            }
            selectedItem = it
            updateDirectory()
        }
    }

    suspend fun updateDirectory() {
        appState.workspace?.let {
            val list = FileHandler.walkPathDepthFirst(it, FileHandler.WalkBehavior.FoldersFirst)
            if (list != directory) {
                println("Workspace directory updated.")
                directory.clear()
                directory.addAll(list)
            }
        }
        selectedItem?.let { path ->
            if (Files.notExists(path))
                selectedItem = null
        }
        appState.file?.let { path ->
            if (Files.notExists(path)) {
                appState.file = null
                appState.fileText = TextFieldValue()
                appState.fileOriginalText = ""
            }
        }
    }
}