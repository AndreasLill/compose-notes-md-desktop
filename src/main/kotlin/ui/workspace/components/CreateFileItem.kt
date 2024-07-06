package ui.workspace.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composenotesmd.desktop.composenotesmd.generated.resources.Res
import com.example.composenotesmd.desktop.composenotesmd.generated.resources.description_24dp
import io.FileHandler
import kotlinx.coroutines.launch
import model.ApplicationState
import org.jetbrains.compose.resources.painterResource

@Composable
fun CreateFileItem(appState: ApplicationState, focusRequester: FocusRequester, onRefreshPoll: () -> Unit, onCreateFile: (Boolean) -> Unit) {
    val createFileText = remember { mutableStateOf(TextFieldValue("")) }
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp).background(MaterialTheme.colors.primary.copy(0.10f)),
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
                            scope.launch {
                                appState.file = FileHandler.createFile(appState.workspace, createFileText.value.text)
                                createFileText.value = TextFieldValue("")
                                onCreateFile(false)
                                onRefreshPoll()
                            }
                            true
                        }
                        else if (it.key == Key.Escape && it.type == KeyEventType.KeyDown) {
                            createFileText.value = TextFieldValue("")
                            onCreateFile(false)
                            true
                        }
                        else false
                    },
                    value = createFileText.value,
                    onValueChange = { createFileText.value = it },
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