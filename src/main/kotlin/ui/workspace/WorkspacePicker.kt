package ui.workspace

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import io.github.vinceglb.filekit.compose.rememberDirectoryPickerLauncher
import model.ApplicationState
import model.enums.Action
import java.nio.file.Paths

@Composable
fun WorkspacePicker(appState: ApplicationState) {
    val folderPicker = rememberDirectoryPickerLauncher(
        title = "Select a workspace",
        initialDirectory = "",
        onResult = {
            it?.path?.let { path ->
                appState.workspace = Paths.get(path)
                appState.file = null
            }
        }
    )

    LaunchedEffect(Unit) {
        appState.event.collect {
            if (it == Action.ChangeWorkspace) {
                folderPicker.launch()
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