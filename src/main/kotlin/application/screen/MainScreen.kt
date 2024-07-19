package application.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import application.model.ApplicationState
import application.ui.Tooltip
import editor.ui.Editor
import workspace.ui.WorkspaceFileViewer
import workspace.ui.WorkspacePanel
import workspace.ui.WorkspacePicker

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainScreen(appState: ApplicationState) {
    Surface(modifier = Modifier.onPointerEvent(PointerEventType.Exit) { appState.isCtrlPressed = false }.onPointerEvent(PointerEventType.Enter) { appState.isCtrlPressed = false }) {
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
}
