package workspace.ui

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import application.model.Action
import application.model.ApplicationState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import workspace.model.WorkspaceViewModel

@Composable
fun WorkspaceFileViewer(appState: ApplicationState)  {
    val viewModel = remember(appState.workspace) { WorkspaceViewModel(appState) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(appState.workspace) {
        if (appState.workspace == null) {
            return@LaunchedEffect
        }

        println("${appState.workspace} polling started.")
        while (true) {
            viewModel.updateDirectory()
            delay(1000)
        }
    }

    LaunchedEffect(appState.workspace) {
        if (appState.workspace == null) {
            return@LaunchedEffect
        }

        appState.event.collect { event ->
            if (event == Action.NewFile) {
                appState.workspace?.let { path ->
                    viewModel.createFile(path)
                }
            }
            if (event == Action.NewFolder) {
                appState.workspace?.let { path ->
                    viewModel.createFolder(path)
                }
            }
        }
    }

    if (appState.workspace != null) {
        Column(modifier = Modifier.fillMaxSize()) {
            viewModel.directory.forEach { path ->
                WorkspaceFile(
                    path = path,
                    depth = (path.parent.toString().toCharArray().count { it == '\\' || it == '/' } - appState.workspace.toString().toCharArray().count { it == '\\' || it == '/' }),
                    visible = path.parent.toString() == appState.workspace.toString() || viewModel.openFolders.contains(path.parent),
                    unsavedChanges = appState.file == path && appState.unsavedChanges,
                    isOpenFile = appState.file == path,
                    isOpenFolder = viewModel.openFolders.contains(path),
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
                                        viewModel.selectItem(path)
                                    }
                                },
                                onConfirm = {
                                    scope.launch {
                                        appState.saveChanges()
                                        viewModel.selectItem(path)
                                    }
                                }
                            )
                        } else {
                            viewModel.selectItem(path)
                        }
                    },
                    onNewFile = {
                        scope.launch {
                            viewModel.createFile(path)
                        }
                    },
                    onNewFolder = {
                        scope.launch {
                            viewModel.createFolder(path)
                        }
                    },
                    onOpenInExplorer = {
                        viewModel.openInExplorer(path)
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
                                        appState.file = path
                                    }
                                },
                                onConfirm = {
                                    scope.launch {
                                        appState.saveChanges()
                                        appState.file = path
                                    }
                                }
                            )
                        } else {
                            appState.file = path
                        }
                    },
                    onRename = {
                        scope.launch {
                            viewModel.renamePath(path, it)
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
                                    viewModel.deletePath(path)
                                }
                            }
                        )
                    },
                )
            }
            ContextMenuArea(
                items = {
                    listOf(
                        ContextMenuItem(
                            label = "New File",
                            onClick = {
                                scope.launch {
                                    appState.workspace?.let {
                                        viewModel.createFile(it)
                                    }
                                }
                            },
                        ),
                        ContextMenuItem(
                            label = "New Folder",
                            onClick = {
                                scope.launch {
                                    appState.workspace?.let {
                                        viewModel.createFolder(it)
                                    }
                                }
                            },
                        ),
                        ContextMenuItem(
                            label = "Open In Explorer",
                            onClick = {
                                appState.workspace?.let {
                                    viewModel.openInExplorer(it)
                                }
                            },
                        ),
                    )
                },
                content = {
                    Box(modifier = Modifier.fillMaxSize())
                }
            )
        }
    }
}