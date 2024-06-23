package screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import model.ApplicationState

@Composable
fun WorkspaceScreen(appState: ApplicationState) {
    var showPicker by remember { mutableStateOf(false) }
    var workspace by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            if (workspace.isBlank()) {
                Text("No workspace selected!")
            }
            else {
                Text(workspace)
            }
            Button(
                onClick = {
                    showPicker = true
                },
                content = {
                    Text("Select Workspace")
                }
            )
            Button(
                onClick = {
                    appState.workspace = workspace
                },
                content = {
                    Text("OK")
                }
            )
            DirectoryPicker(showPicker) { path ->
                workspace = path ?: ""
                showPicker = false
            }
        }
    }
}