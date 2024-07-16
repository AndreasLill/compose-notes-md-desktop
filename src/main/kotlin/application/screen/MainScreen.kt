package application.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import application.model.ApplicationState
import editor.ui.Editor
import workspace.ui.WorkspaceFileViewer
import workspace.ui.WorkspacePanel
import workspace.ui.WorkspacePicker

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainScreen(appState: ApplicationState) {
    Surface(modifier = Modifier.onPointerEvent(PointerEventType.Exit) { appState.isCtrlPressed = false }.onPointerEvent(PointerEventType.Enter) { appState.isCtrlPressed = false }) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.width(300.dp).fillMaxHeight().background(MaterialTheme.colors.background)) {
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
            Divider(
                modifier = Modifier.width(1.dp).fillMaxHeight(),
                color = MaterialTheme.colors.primary
            )
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.surface)) {
                Editor(appState = appState)
            }
        }
    }
}
