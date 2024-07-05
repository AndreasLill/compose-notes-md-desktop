package ui.workspace

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.ApplicationState
import model.enums.Action

@Composable
fun Workspace(appState: ApplicationState) {
    val focusRequester = remember { FocusRequester() }
    val focusScope = rememberCoroutineScope()

    WorkspacePanel(
        onNewFile = {
            appState.action = Action.NewFile
            appState.file = null
            focusScope.launch {
                delay(100)
                try {
                    focusRequester.requestFocus()
                }
                catch(_: Exception) {}
            }
        }
    )
    if (appState.workspace.isNotBlank()) {
        FileViewer(
            appState = appState,
            focusRequester = focusRequester
        )
    }
    if (appState.workspace.isBlank()) {
        WorkspacePicker(
            appState = appState
        )
    }
}