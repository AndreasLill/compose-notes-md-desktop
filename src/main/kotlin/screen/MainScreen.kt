package screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import component.Editor
import component.Files
import component.WorkspaceControls
import component.Workspace
import model.ApplicationState

@Composable
fun MainScreen(appState: ApplicationState) {
    Surface {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.width(300.dp).fillMaxHeight().background(MaterialTheme.colors.background).padding(16.dp)) {
                WorkspaceControls(
                    modifier = Modifier.fillMaxWidth(),
                    appState = appState
                )
                if (appState.workspace.isNotBlank()) {
                    Files(
                        modifier = Modifier.fillMaxWidth(),
                        appState = appState
                    )
                }
                if (appState.workspace.isBlank()) {
                    Workspace(
                        modifier = Modifier.fillMaxWidth(),
                        appState = appState
                    )
                }
            }
            Divider(
                modifier = Modifier.width(1.dp).fillMaxHeight(),
                color = MaterialTheme.colors.primary
            )
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.surface).padding(16.dp)) {
                if (appState.workspace.isNotBlank() && appState.file != null) {
                    Editor(appState)
                }
            }
        }
    }
}
