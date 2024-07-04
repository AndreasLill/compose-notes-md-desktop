package component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composenotesmd.desktop.composenotesmd.generated.resources.Res
import com.example.composenotesmd.desktop.composenotesmd.generated.resources.description_24dp
import io.FileHandler
import model.ApplicationState
import model.DirectoryState
import model.enums.Action
import org.jetbrains.compose.resources.painterResource

@Composable
fun Files(modifier: Modifier, appState: ApplicationState)  {
    val focusRequester = remember { FocusRequester() }
    val state = remember(appState.workspace) { DirectoryState(appState.workspace) }
    val directory = state.directory.collectAsState(null)
    val newFileName = remember { mutableStateOf("") }

    Column(modifier = modifier) {
        if (appState.action == Action.NewFile) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.Transparent,
                shape = RectangleShape,
                elevation = 0.dp,
                content = {
                    Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(Res.drawable.description_24dp),
                            contentDescription = null
                        )
                        BasicTextField(
                            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester).onPreviewKeyEvent {
                                if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                                    FileHandler.createFile(appState.workspace, newFileName.value)
                                    appState.action = Action.None
                                    true
                                }
                                false
                            },
                            value = newFileName.value,
                            onValueChange = { newFileName.value = it },
                            textStyle = LocalTextStyle.current.copy(
                                color = MaterialTheme.colors.primary,
                                fontSize = 13.sp,
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colors.primary),
                            singleLine = true,
                            maxLines = 1,
                        )
                    }
                }
            )
        }

        directory.value?.forEach {
            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    appState.file = it
                    appState.title = "${appState.workspace} - ${appState.file?.name}"
                },
                backgroundColor = if (appState.file == it) MaterialTheme.colors.primary.copy(alpha = 0.20f) else Color.Transparent,
                shape = RectangleShape,
                elevation = 0.dp,
                content = {
                    Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(Res.drawable.description_24dp),
                            contentDescription = null
                        )
                        Text(
                            text = it.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                },
            )
        }
    }
}