package ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import kotlinx.coroutines.launch
import model.ApplicationState
import model.DirectoryState
import model.enums.Action
import org.jetbrains.compose.resources.painterResource

@Composable
fun FileViewer(appState: ApplicationState, focusRequester: FocusRequester)  {
    val state = remember(appState.workspace) { DirectoryState(appState.workspace) }
    val directory = state.directory.collectAsState(null)
    val tempFileName = remember { mutableStateOf("") }
    val fileScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth()) {
        if (appState.action == Action.NewFile) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp).background(MaterialTheme.colors.primary.copy(alpha = 0.20f)),
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
                                    fileScope.launch {
                                        appState.file = FileHandler.createFile(appState.workspace, tempFileName.value)
                                        appState.action = Action.None
                                        tempFileName.value = ""
                                    }
                                    true
                                }
                                else if (it.key == Key.Escape && it.type == KeyEventType.KeyDown) {
                                    appState.action = Action.None
                                    tempFileName.value = ""
                                    true
                                }
                                else false
                            },
                            value = tempFileName.value,
                            onValueChange = { tempFileName.value = it },
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
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp).clickable {
                    appState.file = it
                },
                backgroundColor = if (appState.file == it && appState.action != Action.NewFile) MaterialTheme.colors.primary.copy(alpha = 0.20f) else Color.Transparent,
                shape = RoundedCornerShape(2.dp),
                elevation = 0.dp,
                content = {
                    Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(Res.drawable.description_24dp),
                            contentDescription = null
                        )
                        Text(
                            text = it.nameWithoutExtension,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                },
            )
        }
    }
}