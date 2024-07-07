package ui.workspace

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import model.ApplicationState
import model.enums.Action
import java.nio.file.Paths

@Composable
fun WorkspacePicker(appState: ApplicationState) {
    val showPicker = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        appState.event.collect {
            if (it == Action.ChangeWorkspace) {
                showPicker.value = true
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (appState.workspace == null) {
            Button(
                onClick = {
                    showPicker.value = true
                },
                content = {
                    Text("Select Workspace")
                }
            )
        }
        DirectoryPicker(showPicker.value) { path ->
            path?.let {
                appState.workspace = Paths.get(path)
                appState.file = null
            }
            showPicker.value = false
        }
    }
}