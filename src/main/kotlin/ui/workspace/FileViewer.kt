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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.ApplicationState
import model.enums.Action
import org.jetbrains.compose.resources.painterResource
import java.io.File

@Composable
fun FileViewer(appState: ApplicationState)  {
    val directory = remember { mutableStateOf<List<File>?>(null) }
    val refreshPoll = remember(appState.workspace) { mutableStateOf(false) }
    val tempFileName = remember { mutableStateOf("") }
    val tempNewFile = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(refreshPoll.value) {
        if (refreshPoll.value) {
            refreshPoll.value = false
            return@LaunchedEffect
        }

        println("Workspace polling started.")
        while (true) {
            val temp = File(appState.workspace).listFiles()?.filter { it.extension == "md" }?.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
            temp?.let {
                directory.value = it
            }
            println("${appState.workspace} polled.")
            delay(1000)
        }
    }

    LaunchedEffect(appState.workspace) {
        appState.event.collect {
            if (it == Action.NewFile) {
                tempNewFile.value = true
                appState.file = null
                delay(100)
                try {
                    focusRequester.requestFocus()
                } catch(_: Exception) {}
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (tempNewFile.value) {
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
                                    scope.launch {
                                        appState.file = FileHandler.createFile(appState.workspace, tempFileName.value)
                                        refreshPoll.value = true
                                        tempNewFile.value = false
                                        tempFileName.value = ""
                                    }
                                    true
                                }
                                else if (it.key == Key.Escape && it.type == KeyEventType.KeyDown) {
                                    tempNewFile.value = false
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
                backgroundColor = if (appState.file == it && !tempNewFile.value) MaterialTheme.colors.primary.copy(alpha = 0.20f) else Color.Transparent,
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