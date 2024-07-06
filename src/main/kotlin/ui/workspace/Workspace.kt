package ui.workspace

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import model.ApplicationState
import model.enums.Action

@Composable
fun Workspace(appState: ApplicationState) {
    val scope = rememberCoroutineScope()

    WorkspacePanel(
        onNewFile = {
            scope.launch {
                appState.event.emit(Action.NewFile)
            }
        }
    )
    if (appState.workspace.isNotBlank()) {
        FileViewer(
            appState = appState
        )
    }
    if (appState.workspace.isBlank()) {
        WorkspacePicker(
            appState = appState
        )
    }
}