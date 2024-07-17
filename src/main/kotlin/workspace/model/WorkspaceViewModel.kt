package workspace.model

import androidx.compose.runtime.mutableStateListOf
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

    fun selectItem(path: Path) {
        if (path.isDirectory() && !openFolders.contains(path)) {
            openFolders.add(path)
        } else if (path.isDirectory() && openFolders.contains(path)) {
            openFolders.removeIf { it.toString().contains(path.toString()) }
        } else if (!path.isDirectory()) {
            appState.file = path
        }
    }

    fun openInExplorer(path: Path) {
        val dirPath = if (path.isDirectory()) path else path.parent
        Desktop.getDesktop().open(dirPath.toFile())
    }

    suspend fun createFile(path: Path) {
        val dirPath = if (path.isDirectory()) path else path.parent
        FileHandler.create(dirPath, false)?.let {
            updateDirectory()
            appState.file = it
            if (path.isDirectory()) {
                openFolders.add(path)
            }
        }
    }

    suspend fun createFolder(path: Path) {
        val dirPath = if (path.isDirectory()) path else path.parent
        FileHandler.create(dirPath, true)?.let {
            updateDirectory()
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
            updateDirectory()
        }
    }

    suspend fun updateDirectory() {
        appState.workspace?.let {
            val list = FileHandler.walkPathDepthFirst(it, FileHandler.WalkBehavior.FoldersFirst)
            if (list != directory) {
                directory.clear()
                directory.addAll(list)
            }
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