package workspace.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import io.FileHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import application.model.ApplicationState
import application.model.Action
import java.awt.Desktop
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory

@Composable
fun WorkspaceFileViewer(appState: ApplicationState)  {
    val directory = remember { mutableStateListOf<Path>() }
    val openFolders = remember(appState.workspace) { mutableStateListOf<Path>() }
    val refreshPoll = remember(appState.workspace) { mutableStateOf(false) }
    val selectedItem = remember(appState.workspace) { mutableStateOf<Path?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(appState.workspace, refreshPoll.value) {
        if (appState.workspace == null) {
            return@LaunchedEffect
        }
        if (refreshPoll.value) {
            refreshPoll.value = false
            return@LaunchedEffect
        }

        println("Workspace polling started.")
        while (true) {
            appState.workspace?.let {
                val list = FileHandler.walkPathDepthFirst(it, FileHandler.WalkBehavior.FoldersFirst)
                if (list != directory) {
                    println("Workspace directory updated.")
                    directory.clear()
                    directory.addAll(list)
                }
            }
            selectedItem.value?.let { path ->
                if (Files.notExists(path))
                    selectedItem.value = null
            }
            appState.file?.let { path ->
                if (Files.notExists(path)) {
                    appState.file = null
                    appState.fileText = TextFieldValue()
                    appState.fileOriginalText = ""
                }
            }
            println("${appState.workspace} polled.")
            delay(1000)
        }
    }

    LaunchedEffect(appState.workspace) {
        if (appState.workspace == null) {
            return@LaunchedEffect
        }

        appState.event.collect { event ->
            when (event) {
                Action.NewFile -> {
                    selectedItem.value?.let { path ->
                        if (path.isDirectory()) {
                            FileHandler.createFile(path)?.let {
                                refreshPoll.value = true
                                appState.file = it
                                selectedItem.value = it
                            }
                        } else {
                            FileHandler.createFile(path.parent)?.let {
                                refreshPoll.value = true
                                appState.file = it
                                selectedItem.value = it
                            }
                        }
                        return@collect
                    }
                    appState.workspace?.let { path ->
                        FileHandler.createFile(path)?.let {
                            refreshPoll.value = true
                            appState.file = it
                            selectedItem.value = it
                        }
                        return@collect
                    }
                }
                Action.NewFolder -> {
                    selectedItem.value?.let { path ->
                        if (path.isDirectory()) {
                            FileHandler.createFolder(path)?.let {
                                refreshPoll.value = true
                                selectedItem.value = it
                                openFolders.add(it)
                            }
                        } else {
                            FileHandler.createFolder(path.parent)?.let {
                                refreshPoll.value = true
                                selectedItem.value = it
                                openFolders.add(it)
                            }
                        }
                        return@collect
                    }
                    appState.workspace?.let { path ->
                        FileHandler.createFolder(path)?.let {
                            refreshPoll.value = true
                            selectedItem.value = it
                            openFolders.add(it)
                        }
                        return@collect
                    }
                }
                else -> {}
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        directory.forEach { path ->
            WorkspaceFile(
                path = path,
                depth = (path.parent.toString().toCharArray().count { it == '\\' } - appState.workspace.toString().toCharArray().count { it == '\\' }),
                visible = path.parent.toString() == appState.workspace.toString() || openFolders.contains(path.parent),
                unsavedChanges = appState.file == path && appState.unsavedChanges,
                selected = selectedItem.value == path,
                selectedFile = appState.file == path,
                isOpenFolder = openFolders.contains(path),
                onClick = {
                    if (appState.unsavedChanges) {
                        appState.confirmDialog.showDialog(
                            title = "Unsaved Changes",
                            body = "There are unsaved changes in '${appState.file?.fileName}'\nDo you want to save the changes?",
                            buttonCancel = "Cancel",
                            buttonDiscard = "Discard",
                            buttonConfirm = "Save Changes",
                            onDiscard = {
                                scope.launch {
                                    appState.discardChanges()
                                    if (path.isDirectory() && !openFolders.contains(path) && selectedItem.value == path) {
                                        openFolders.add(path)
                                    } else if (path.isDirectory() && openFolders.contains(path) && selectedItem.value == path) {
                                        openFolders.removeIf { it.toString().contains(path.toString()) }
                                    } else if (!path.isDirectory()) {
                                        appState.file = path
                                    }
                                    selectedItem.value = path
                                }
                            },
                            onConfirm = {
                                scope.launch {
                                    appState.saveChanges()
                                    if (path.isDirectory() && !openFolders.contains(path) && selectedItem.value == path) {
                                        openFolders.add(path)
                                    } else if (path.isDirectory() && openFolders.contains(path) && selectedItem.value == path) {
                                        openFolders.removeIf { it.toString().contains(path.toString()) }
                                    } else if (!path.isDirectory()) {
                                        appState.file = path
                                    }
                                    selectedItem.value = path
                                }
                            }
                        )
                    } else  {
                        if (path.isDirectory() && !openFolders.contains(path) && selectedItem.value == path) {
                            openFolders.add(path)
                        } else if (path.isDirectory() && openFolders.contains(path) && selectedItem.value == path) {
                            openFolders.removeIf { it.toString().contains(path.toString()) }
                        } else if (!path.isDirectory()) {
                            appState.file = path
                        }
                        selectedItem.value = path
                    }
                },
                onOpenInExplorer = {
                    if (path.isDirectory()) {
                        Desktop.getDesktop().open(path.toFile())
                    } else {
                        Desktop.getDesktop().open(path.parent.toFile())
                    }
                },
                onBeginRename = {
                    if (appState.unsavedChanges) {
                        appState.confirmDialog.showDialog(
                            title = "Unsaved Changes",
                            body = "There are unsaved changes in '${appState.file?.fileName}'\nDo you want to save the changes?",
                            buttonCancel = "Cancel",
                            buttonDiscard = "Discard",
                            buttonConfirm = "Save Changes",
                            onDiscard = {
                                scope.launch {
                                    appState.discardChanges()
                                    selectedItem.value = path
                                    appState.file = path
                                }
                            },
                            onConfirm = {
                                scope.launch {
                                    appState.saveChanges()
                                    selectedItem.value = path
                                    appState.file = path
                                }
                            }
                        )
                    } else {
                        selectedItem.value = path
                        appState.file = path
                    }
                },
                onRename = {
                    scope.launch {
                        FileHandler.rename(path, it)?.let {
                            if (!it.isDirectory()) {
                                appState.file = it
                            }
                            selectedItem.value = it
                            refreshPoll.value = true
                        }
                    }
                },
                onDelete = {
                    appState.confirmDialog.showDialog(
                        title = "Delete",
                        body = "Are you sure you want to delete '${path.fileName}'?\nIt will be moved to the recycle bin.",
                        buttonCancel = "Cancel",
                        buttonConfirm = "Delete",
                        onCancel = {
                            println("Cancel")
                        },
                        onConfirm = {
                            scope.launch {
                                FileHandler.delete(path)
                                refreshPoll.value = true
                            }
                        }
                    )
                },
            )
        }
    }
}