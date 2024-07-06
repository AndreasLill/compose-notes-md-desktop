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
import model.enums.Action

@Composable
fun WorkspacePicker(appState: ApplicationState) {
    var showPicker = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        appState.event.collect {
            if (it == Action.ChangeWorkspace) {
                showPicker.value = true
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (appState.workspace.isBlank()) {
            Button(
                onClick = {
                    showPicker.value = true
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
        }
        DirectoryPicker(showPicker.value) { path ->
            path?.let {
                appState.workspace = path
                appState.file = null
            }
            showPicker.value = false
        }
    }
}