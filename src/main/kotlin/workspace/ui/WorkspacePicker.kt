package workspace.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import io.github.vinceglb.filekit.compose.rememberDirectoryPickerLauncher
import application.model.ApplicationState
import application.model.Action
import application.io.FileHandler
import kotlinx.coroutines.launch
import java.nio.file.Paths

@Composable
fun WorkspacePicker(appState: ApplicationState) {
    val scope = rememberCoroutineScope()
    val folderPicker = rememberDirectoryPickerLauncher(
        title = "Select a workspace folder.",
        onResult = {
            it?.path?.let { path ->
                scope.launch {
                    if (FileHandler.isValidWorkspace(Paths.get(path))) {
                        appState.workspace = Paths.get(path)
                        appState.file = null
                    } else {
                        // TODO: Add notification with invalid workspace error.
                    }
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        appState.event.collect { event ->
            if (event.action == Action.ChangeWorkspace) {
                folderPicker.launch()
                event.callback()
            }
        }
    }

    if (appState.workspace == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "No workspace is open",
                    fontSize = 20.sp,
                )
                TextButton(
                    onClick = {
                        folderPicker.launch()
                    },
                    content = {
                        Text("Select workspace")
                    }
                )
            }
        }
    }
}