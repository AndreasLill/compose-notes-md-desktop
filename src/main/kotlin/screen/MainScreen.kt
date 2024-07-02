package screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import component.Editor
import component.Files
import component.Workspace
import model.ApplicationState

@Composable
fun MainScreen(appState: ApplicationState) {
    Surface {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.width(300.dp).fillMaxHeight().background(MaterialTheme.colors.background).padding(16.dp)) {
                Files(modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth(), appState = appState)
                Workspace(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(), appState = appState)
            }
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.surface).padding(16.dp)) {
                if (appState.workspace.isNotBlank() && appState.file != null) {
                    Editor(appState)
                }
            }
        }
    }
}
