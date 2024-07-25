package workspace.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import application.model.Action
import application.model.ApplicationEvent
import application.model.ApplicationState
import application.ui.Tooltip
import com.andreaslill.composenotesmd.desktop.composenotesmd.generated.resources.Res
import com.andreaslill.composenotesmd.desktop.composenotesmd.generated.resources.change_workspace_24dp
import com.andreaslill.composenotesmd.desktop.composenotesmd.generated.resources.create_new_file_24dp
import com.andreaslill.composenotesmd.desktop.composenotesmd.generated.resources.create_new_folder_24dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun WorkspacePanel(appState: ApplicationState) {
    val scope = rememberCoroutineScope()

    if (appState.workspace != null) {
        Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp).padding(horizontal = 16.dp)) {
            Tooltip(appState.workspace.toString()) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        scope.launch {
                            appState.event.emit(ApplicationEvent(Action.ChangeWorkspace))
                        }
                    },
                    content = {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                modifier = Modifier.align(Alignment.CenterStart).padding(end = 24.dp),
                                text = appState.getWorkspaceShortString(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 12.sp,
                                color = MaterialTheme.colors.onSurface
                            )
                            Icon(
                                modifier = Modifier.size(20.dp).align(Alignment.CenterEnd),
                                painter = painterResource(Res.drawable.change_workspace_24dp),
                                tint = MaterialTheme.colors.onSurface,
                                contentDescription = "Change Workspace"
                            )
                        }
                    }
                )
            }
        }
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).padding(bottom = 8.dp)) {
            Row(modifier = Modifier.align(Alignment.CenterStart), verticalAlignment = Alignment.CenterVertically) {
                Tooltip("New File") {
                    IconButton(
                        onClick = {
                            scope.launch {
                                appState.event.emit(ApplicationEvent(Action.NewFile))
                            }
                        },
                        content = {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(Res.drawable.create_new_file_24dp),
                                contentDescription = "New File"
                            )
                        }
                    )
                }
                Tooltip("New Folder") {
                    IconButton(
                        onClick = {
                            scope.launch {
                                appState.event.emit(ApplicationEvent(Action.NewFolder))
                            }
                        },
                        content = {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(Res.drawable.create_new_folder_24dp),
                                contentDescription = "New Folder"
                            )
                        }
                    )
                }
            }
        }
    }
}