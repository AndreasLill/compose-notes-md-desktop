package component

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import model.ApplicationState

@Composable
fun Workspace(modifier: Modifier, appState: ApplicationState) {
    var showPicker by remember { mutableStateOf(false) }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (appState.workspace.isBlank()) {
            Text(
                text = "No workspace selected!",
                fontSize = 13.sp,
            )
            Text(
                text = "Select a folder to use as a workspace.",
                fontSize = 13.sp,
            )
        }
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
                if (path.isBlank()) {
                    appState.title = "No Workspace Selected!"
                }
                else {
                    appState.title = path
                }
            }
            showPicker = false
        }
    }
}