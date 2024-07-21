import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import application.input.ShortcutHandler
import application.io.SaveHandler
import application.model.Action
import application.model.ApplicationEvent
import application.theme.ColorScheme
import application.ui.ConfirmDialog
import application.ui.Tooltip
import editor.ui.Editor
import kotlinx.coroutines.launch
import workspace.ui.WorkspaceFileViewer
import workspace.ui.WorkspacePanel
import workspace.ui.WorkspacePicker

@OptIn(ExperimentalComposeUiApi::class)
fun main(args: Array<String>) = application {
    val appState = remember { SaveHandler.loadState(args) }
    val scope = rememberCoroutineScope()
    val windowTitle = remember(appState.workspace, appState.file, appState.unsavedChanges) {
        when {
            (appState.file != null && !appState.unsavedChanges) -> "Compose Notes - ${appState.file?.fileName}"
            (appState.file != null && appState.unsavedChanges) -> "Compose Notes - ${appState.file?.fileName}*"
            else -> "Compose Notes"
        }
    }
    val contextMenuRepresentation = if (isSystemInDarkTheme()) DarkDefaultContextMenuRepresentation else LightDefaultContextMenuRepresentation

    Window(
        onCloseRequest = {
            if (appState.unsavedChanges) {
                appState.confirmDialog.showDialog(
                    title = "Unsaved Changes",
                    body = "There are unsaved changes in '${appState.file?.fileName}'\nDo you want to save the changes?",
                    buttonCancel = "Cancel",
                    buttonDiscard = "Discard",
                    buttonConfirm = "Save Changes",
                    onDiscard = {
                        SaveHandler.saveState(appState)
                        exitApplication()
                    },
                    onConfirm = {
                        scope.launch {
                            appState.event.emit(ApplicationEvent(Action.SaveFile) {
                                SaveHandler.saveState(appState)
                                exitApplication()
                            })
                        }
                    }
                )
            } else {
                SaveHandler.saveState(appState)
                exitApplication()
            }
        },
        title = windowTitle,
        state = appState.windowState,
        onPreviewKeyEvent = {
            ShortcutHandler.event(scope, appState, it)
        },
        content = {
            MaterialTheme(colors = ColorScheme.Default) {
                CompositionLocalProvider(LocalContextMenuRepresentation provides contextMenuRepresentation) {
                    Surface(modifier = Modifier.onPointerEvent(PointerEventType.Exit) { appState.isCtrlPressed = false }.onPointerEvent(
                        PointerEventType.Enter) { appState.isCtrlPressed = false }) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            if (appState.workspaceEnabled) {
                                Column(modifier = Modifier.width(appState.workspaceWidth.dp).fillMaxHeight().background(MaterialTheme.colors.background)) {
                                    WorkspacePanel(
                                        appState = appState
                                    )
                                    WorkspaceFileViewer(
                                        appState = appState
                                    )
                                    WorkspacePicker(
                                        appState = appState
                                    )
                                }
                                Tooltip("Drag To Resize") {
                                    Divider(
                                        modifier = Modifier.width(2.dp).fillMaxHeight().pointerHoverIcon(PointerIcon.Hand).draggable(
                                            state = rememberDraggableState { delta ->
                                                appState.workspaceWidth += delta
                                            },
                                            startDragImmediately = true,
                                            orientation = Orientation.Horizontal,
                                            onDragStarted = { },
                                            onDragStopped = { },
                                        ),
                                        color = MaterialTheme.colors.primary
                                    )
                                }
                            }
                            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.surface)) {
                                Editor(appState = appState)
                            }
                        }
                    }
                    ConfirmDialog(
                        show = appState.confirmDialog.show,
                        title = appState.confirmDialog.title,
                        body = appState.confirmDialog.body,
                        cancelButton = appState.confirmDialog.buttonCancel,
                        discardButton = appState.confirmDialog.buttonDiscard,
                        confirmButton = appState.confirmDialog.buttonConfirm,
                        onCancel = {
                            appState.confirmDialog.listenerOnCancel.invoke()
                            appState.confirmDialog.closeDialog()
                        },
                        onDiscard = {
                            appState.confirmDialog.listenerOnDiscard.invoke()
                            appState.confirmDialog.closeDialog()
                        },
                        onConfirm = {
                            appState.confirmDialog.listenerOnConfirm.invoke()
                            appState.confirmDialog.closeDialog()
                        }
                    )
                }
            }
        }
    )
}
