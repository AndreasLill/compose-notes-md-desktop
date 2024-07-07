package ui.workspace

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import io.FileHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.ApplicationState
import model.enums.Action
import java.awt.Desktop
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.isDirectory

@Composable
fun WorkspaceFileViewer(appState: ApplicationState)  {
    val directory = remember { mutableStateListOf<Path>() }
    val openFolders = remember { mutableStateListOf<Path>() }
    val refreshPoll = remember(appState.workspace) { mutableStateOf(false) }
    val selectedItem = remember { mutableStateOf<Path?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(refreshPoll.value) {
        if (refreshPoll.value) {
            refreshPoll.value = false
            return@LaunchedEffect
        }

        println("Workspace polling started.")
        while (true) {
            Files.walk(appState.workspace, 10).filter { (Files.isDirectory(it) && it.toString() != appState.workspace.toString()) || it.extension == "md" }.toList().let {
                directory.clear()
                directory.addAll(it)
            }
            println("${appState.workspace} polled.")
            delay(1000)
        }
    }

    LaunchedEffect(appState.workspace) {
        appState.event.collect { event ->
            if (event == Action.NewFile) {
                appState.workspace?.let { workspace ->
                    FileHandler.createFile(workspace, "name.md")
                    //FileHandler.renameFile(workspace.toPath(), "name.md", "newname.md")
                    // TODO with dialog.
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        directory.forEach { path ->
            WorkspaceFile(
                path = path,
                depth = (path.parent.toString().toCharArray().count { it == '\\' } - appState.workspace.toString().toCharArray().count { it == '\\' }),
                visible = path.parent.toString() == appState.workspace.toString() || openFolders.contains(path.parent),
                selected = selectedItem.value == path,
                selectedFile = appState.file == path,
                isOpenFolder = openFolders.contains(path),
                onClick = {
                    selectedItem.value = path

                    if (path.isDirectory()) {
                        if (!openFolders.contains(path)) {
                            openFolders.add(path)
                        } else {
                            openFolders.removeIf {
                                it.toString().contains(path.toString())
                            }
                        }
                    }
                    else {
                        appState.file = path
                    }
                },
                onOpenInExplorer = {
                    Desktop.getDesktop().open(path.parent.toFile())
                },
                onRename = {
                    // TODO with dialog.
                },
                onDelete = {
                    scope.launch {
                        FileHandler.deleteFile(path).let { success ->
                            if (success) {
                                refreshPoll.value = true
                            }
                        }
                    }
                },
            )
        }
    }
}