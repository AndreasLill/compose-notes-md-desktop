package ui.workspace

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
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
import java.awt.Desktop
import java.io.File

@Composable
fun FileViewer(appState: ApplicationState)  {
    val directory = remember { mutableStateOf<List<File>?>(null) }
    val refreshPoll = remember(appState.workspace) { mutableStateOf(false) }
    val createFileText = remember { mutableStateOf(TextFieldValue("")) }
    val createFile = remember { mutableStateOf(false) }
    val renameFileText = remember { mutableStateOf(TextFieldValue("")) }
    val renameFileName = remember { mutableStateOf("") }
    val createFileFocusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(refreshPoll.value) {
        if (refreshPoll.value) {
            refreshPoll.value = false
            return@LaunchedEffect
        }

        println("Workspace polling started.")
        while (true) {
            val temp = appState.workspace?.listFiles()?.filter { it.extension == "md" }?.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
            temp?.let {
                if (directory.value != it) {
                    directory.value = it
                    println("${appState.workspace} updated.")
                }
            }
            println("${appState.workspace} polled.")
            delay(1000)
        }
    }

    LaunchedEffect(appState.workspace) {
        appState.event.collect {
            if (it == Action.NewFile) {
                createFile.value = true
                try {
                    delay(100)
                    createFileFocusRequester.requestFocus()
                } catch(_: Exception) {}
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (createFile.value) {
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
                            modifier = Modifier.fillMaxWidth().focusRequester(createFileFocusRequester).onPreviewKeyEvent {
                                if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                                    scope.launch {
                                        appState.file = FileHandler.createFile(appState.workspace, createFileText.value.text)
                                        refreshPoll.value = true
                                        createFile.value = false
                                        createFileText.value = TextFieldValue("")
                                    }
                                    true
                                }
                                else if (it.key == Key.Escape && it.type == KeyEventType.KeyDown) {
                                    createFile.value = false
                                    createFileText.value = TextFieldValue("")
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

        directory.value?.forEach { file ->
            val renameFileFocusRequester = remember { FocusRequester() }
            val selected = (appState.file == file && !createFile.value)
            ContextMenuArea(
                items = {
                    listOf(
                        ContextMenuItem("Open In Explorer") {
                            val desktop = Desktop.getDesktop()
                            desktop.open(appState.workspace)
                        },
                        ContextMenuItem("Rename") {
                            renameFileName.value = file.name
                            renameFileText.value = TextFieldValue(file.nameWithoutExtension, TextRange(0, file.nameWithoutExtension.length))
                            scope.launch {
                                try {
                                    delay(100)
                                    renameFileFocusRequester.requestFocus()
                                } catch(_: Exception) {}
                            }
                        },
                        ContextMenuItem("Delete") {
                            scope.launch {
                                FileHandler.deleteFile(file).let { success ->
                                    if (success) {
                                        refreshPoll.value = true
                                    }
                                }
                            }
                        },
                    )
                },
                content = {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp).clickable {
                            appState.file = file
                        },
                        backgroundColor = if (selected) MaterialTheme.colors.primary.copy(0.10f) else Color.Transparent,
                        shape = RoundedCornerShape(2.dp),
                        elevation = 0.dp,
                        content = {
                            Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    painter = painterResource(Res.drawable.description_24dp),
                                    tint = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                                    contentDescription = null
                                )
                                if (file.name == renameFileName.value) {
                                    BasicTextField(
                                        modifier = Modifier.fillMaxWidth().focusRequester(renameFileFocusRequester).onPreviewKeyEvent {
                                            if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                                                scope.launch {
                                                    val renamedFile = FileHandler.renameFile(file, renameFileText.value.text)
                                                    appState.file = renamedFile
                                                    refreshPoll.value = true
                                                    renameFileName.value = ""
                                                    renameFileText.value = TextFieldValue("")
                                                }
                                                true
                                            }
                                            else if (it.key == Key.Escape && it.type == KeyEventType.KeyDown) {
                                                renameFileName.value = ""
                                                renameFileText.value = TextFieldValue("")
                                                true
                                            }
                                            else false
                                        },
                                        value = renameFileText.value,
                                        onValueChange = { renameFileText.value = it },
                                        textStyle = LocalTextStyle.current.copy(
                                            color = MaterialTheme.colors.primary,
                                            fontSize = 13.sp,
                                        ),
                                        cursorBrush = SolidColor(MaterialTheme.colors.primary),
                                        singleLine = true,
                                        maxLines = 1,
                                    )
                                } else {
                                    Text(
                                        text = file.nameWithoutExtension,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                                    )
                                }
                            }
                        },
                    )
                }
            )
        }
    }
}