package ui.workspace

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import model.ApplicationState

@Composable
fun WorkspacePicker(appState: ApplicationState) {
    var showPicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = {
                showPicker = true
            },
            content = {
                if (appState.workspace.isBlank()) {
                    Text("Select Workspace")
                }
                else {
                    Text("Change Workspace")
                }
            }
        )
        DirectoryPicker(showPicker) { path ->
            path?.let {
                appState.workspace = path
                appState.file = null
            }
            showPicker = false
        }
    }
}