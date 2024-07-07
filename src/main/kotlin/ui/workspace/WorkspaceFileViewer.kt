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
import java.nio.file.Paths
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.pathString

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
            Files.walk(Paths.get(appState.workspace!!.path), 10).filter { (Files.isDirectory(it) && it.pathString != appState.workspace!!.path) || it.extension == "md" }.toList().let {
                directory.clear()
                directory.addAll(it)
            }
            println("${appState.workspace} polled.")
            delay(1000)
        }
    }

    LaunchedEffect(appState.workspace) {
        appState.event.collect {
            if (it == Action.NewFile) {
                // TODO with dialog.
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        directory.forEach { path ->
            WorkspaceFile(
                path = path,
                depth = (path.parent.pathString.toCharArray().count { it == '\\' } - appState.workspace!!.path.toCharArray().count { it == '\\' }),
                visible = path.parent.pathString == appState.workspace!!.path || openFolders.contains(path.parent),
                selected = selectedItem.value == path,
                selectedFile = appState.file == path.toFile(),
                isOpenFolder = openFolders.contains(path),
                onClick = {
                    selectedItem.value = path

                    if (path.isDirectory()) {
                        if (!openFolders.contains(path)) {
                            openFolders.add(path)
                        } else {
                            openFolders.removeIf {
                                it.pathString.contains(path.pathString)
                            }
                        }
                    }
                    else {
                        appState.file = path.toFile()
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
                        FileHandler.deleteFile(path.toFile()).let { success ->
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