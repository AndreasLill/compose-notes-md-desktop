package ui.workspace

import androidx.compose.runtime.Composable
import model.ApplicationState

@Composable
fun Workspace(appState: ApplicationState) {
    WorkspacePanel(
        appState = appState
    )
    if (appState.workspace != null) {
        WorkspaceFileViewer(
            appState = appState
        )
    }
    WorkspacePicker(
        appState = appState
    )
}