package workspace.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
                viewModel.selectedItem?.let { path ->
                    viewModel.createFile(path)
                    return@collect
                }
                appState.workspace?.let { path ->
                    viewModel.createFile(path)
                    return@collect
                }
            }
            if (event == Action.NewFolder) {
                viewModel.selectedItem?.let { path ->
                    viewModel.createFolder(path)
                    return@collect
                }
                appState.workspace?.let { path ->
                    viewModel.createFolder(path)
                    return@collect
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        viewModel.directory.forEach { path ->
            WorkspaceFile(
                path = path,
                depth = (path.parent.toString().toCharArray().count { it == '\\' } - appState.workspace.toString().toCharArray().count { it == '\\' }),
                visible = path.parent.toString() == appState.workspace.toString() || viewModel.openFolders.contains(path.parent),
                unsavedChanges = appState.file == path && appState.unsavedChanges,
                selected = viewModel.selectedItem == path,
                selectedFile = appState.file == path,
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
                    } else  {
                        viewModel.selectItem(path)
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
                                    viewModel.selectedItem = path
                                    appState.file = path
                                }
                            },
                            onConfirm = {
                                scope.launch {
                                    appState.saveChanges()
                                    viewModel.selectedItem = path
                                    appState.file = path
                                }
                            }
                        )
                    } else {
                        viewModel.selectedItem = path
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
    }
}