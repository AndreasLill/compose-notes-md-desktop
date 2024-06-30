package component

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker

@Composable
fun Workspace(modifier: Modifier, workspace: String, callback: (ws: String) -> Unit) {
    var showPicker by remember { mutableStateOf(false) }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (workspace.isBlank()) {
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
                if (workspace.isBlank()) {
                    Text("Select Workspace")
                }
                else {
                    Text("Change Workspace")
                }
            }
        )
        DirectoryPicker(showPicker) { path ->
            path?.let {
                callback(it)
            }
            showPicker = false
        }
    }
}