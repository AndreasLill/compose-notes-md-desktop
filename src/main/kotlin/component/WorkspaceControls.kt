package component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.composenotesmd.desktop.composenotesmd.generated.resources.Res
import com.example.composenotesmd.desktop.composenotesmd.generated.resources.create_new_folder_24dp
import com.example.composenotesmd.desktop.composenotesmd.generated.resources.note_add_24dp
import com.example.composenotesmd.desktop.composenotesmd.generated.resources.sync_24dp
import model.ApplicationState
import org.jetbrains.compose.resources.painterResource

@Composable
fun WorkspaceControls(modifier: Modifier, appState: ApplicationState) {
    Box(modifier = modifier) {
        Row(modifier = Modifier.align(Alignment.CenterStart), verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = {
                    println("new file")
                },
                content = {
                    Icon(
                        painter = painterResource(Res.drawable.note_add_24dp),
                        contentDescription = null
                    )
                }
            )
            IconButton(
                onClick = {
                    println("new folder")
                },
                content = {
                    Icon(
                        painter = painterResource(Res.drawable.create_new_folder_24dp),
                        contentDescription = null
                    )
                }
            )
        }
        Row(modifier = Modifier.align(Alignment.CenterEnd), verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = {
                    println("change workspace")
                },
                content = {
                    Icon(
                        painter = painterResource(Res.drawable.sync_24dp),
                        contentDescription = null
                    )
                }
            )
        }
    }
}