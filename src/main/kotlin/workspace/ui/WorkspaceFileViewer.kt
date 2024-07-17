package workspace.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import application.model.Action
import application.model.ApplicationState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import workspace.model.WorkspaceViewModel

@Composable
fun WorkspaceFileViewer(appState: ApplicationState)  {
    val viewModel = remember(appState.workspace) { WorkspaceViewModel(appState) }
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(appState.workspace) {
        if (appState.workspace == null) {
            return@LaunchedEffect
        }

        while (true) {
            viewModel.updateDirectory()
            delay(1000)
        }
    }

    LaunchedEffect(Unit) {
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
        /**
         * Background layout for context menu.
         */
        Box(modifier = Modifier.fillMaxSize()) {
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
            /**
             * Lazy list for directory where key is the path.
             */
            LazyColumn(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), state = lazyListState) {
                items(items = viewModel.directory.filter { it.parent == appState.workspace || viewModel.openFolders.contains(it.parent) }, key = { it }) { path ->
                    WorkspaceFile(
                        path = path,
                        depth = (path.parent.toString().toCharArray().count { it == '\\' || it == '/' } - appState.workspace.toString().toCharArray().count { it == '\\' || it == '/' }),
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
                                onCancel = {},
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
            /**
             * Vertical scrollbar for directory lazy list.
             */
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(lazyListState),
                style = LocalScrollbarStyle.current.copy(
                    shape = RectangleShape
                )
            )
        }
    }
}