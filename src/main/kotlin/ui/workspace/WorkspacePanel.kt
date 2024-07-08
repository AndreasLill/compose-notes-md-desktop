package ui.workspace

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.composenotesmd.desktop.composenotesmd.generated.resources.Res
import com.example.composenotesmd.desktop.composenotesmd.generated.resources.create_new_folder_24dp
import com.example.composenotesmd.desktop.composenotesmd.generated.resources.note_add_24dp
import com.example.composenotesmd.desktop.composenotesmd.generated.resources.sync_24dp
import kotlinx.coroutines.launch
import model.ApplicationState
import model.enums.Action
import org.jetbrains.compose.resources.painterResource

@Composable
fun WorkspacePanel(appState: ApplicationState) {
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(modifier = Modifier.align(Alignment.CenterStart), verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = {
                    scope.launch {
                        appState.event.emit(Action.NewFile)
                    }
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
                    scope.launch {
                        appState.event.emit(Action.NewFolder)
                    }
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
                    scope.launch {
                        appState.event.emit(Action.ChangeWorkspace)
                    }
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