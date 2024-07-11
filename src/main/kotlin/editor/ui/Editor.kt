package editor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.FileHandler
import kotlinx.coroutines.launch
import application.model.ApplicationState
import editor.model.EditorState
import application.model.Action

@Composable
fun Editor(appState: ApplicationState) {
    val editorState = remember { EditorState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(appState.file) {
        appState.file?.let {
            appState.fileOriginalText = FileHandler.readFile(it) ?: ""
            appState.fileText = TextFieldValue(appState.fileOriginalText)
        }
    }

    LaunchedEffect(appState.fileOriginalText, appState.fileText) {
        appState.unsavedChanges = appState.fileOriginalText != appState.fileText.text
    }

    LaunchedEffect(appState.file) {
        appState.event.collect { event ->
            if (event == Action.SaveFile) {
                appState.saveChanges()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (appState.file != null) {
            BasicTextField(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                value = appState.fileText,
                onValueChange = { appState.fileText = it },
                textStyle = LocalTextStyle.current.copy(
                    color = MaterialTheme.colors.primary,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                ),
                cursorBrush = SolidColor(MaterialTheme.colors.primary),
                visualTransformation = {
                    TransformedText(
                        text = editorState.getMarkdownAnnotatedString(appState.fileText.text),
                        offsetMapping = OffsetMapping.Identity
                    )
                }
            )
        }
        if (appState.workspace != null && appState.file == null) {
            Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "No file is open",
                    fontSize = 20.sp,
                )
                TextButton(
                    onClick = {
                        scope.launch {
                            appState.event.emit(Action.NewFile)
                        }
                    },
                    content = {
                        Text("Create a new file (CTRL + N)")
                    }
                )
            }
        }
        if (appState.file != null) {
            Column(modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart).background(MaterialTheme.colors.background)) {
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp
                )
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)) {
                    Column(modifier = Modifier.align(Alignment.CenterEnd)) {
                        SelectionContainer {
                            Text(
                                text = if (appState.fileText.selection.length > 0) "${appState.fileText.text.length} characters (${appState.fileText.selection.length} selected)" else "${appState.fileText.text.length} characters",
                                fontSize = 12.sp,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}