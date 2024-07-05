package ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.editor.Editor
import ui.workspace.Workspace
import model.ApplicationState

@Composable
fun MainScreen(appState: ApplicationState) {
    Surface {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.width(300.dp).fillMaxHeight().background(MaterialTheme.colors.background)) {
                Workspace(appState = appState)
            }
            Divider(
                modifier = Modifier.width(1.dp).fillMaxHeight(),
                color = MaterialTheme.colors.primary
            )
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.surface).padding(16.dp)) {
                if (appState.workspace.isNotBlank() && appState.file != null) {
                    Editor(appState = appState)
                }
            }
        }
    }
}
