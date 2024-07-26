package editor.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.selectAll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import application.model.Action
import application.model.ApplicationEvent
import application.model.ApplicationState
import editor.model.EditorViewModel
import kotlinx.coroutines.launch

@Composable
fun Editor(appState: ApplicationState) {
    val viewModel = remember { EditorViewModel() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val clipboard = LocalClipboardManager.current
    val annotatedString = remember(viewModel.editorState.text) { viewModel.getMarkdownAnnotatedString(viewModel.editorState.text.toString()) }

    LaunchedEffect(appState.file) {
        if (appState.file == null)
            return@LaunchedEffect

        viewModel.readFile(appState.file)
    }

    LaunchedEffect(viewModel.originalText, viewModel.editorState.text) {
        appState.unsavedChanges = viewModel.originalText != viewModel.editorState.text
    }

    LaunchedEffect(Unit) {
        appState.event.collect { event ->
            if (event.action == Action.SaveFile) {
                viewModel.saveChanges(appState.file)
                event.callback()
            }
        }
    }

    if (appState.workspace != null && appState.file == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "No file is open",
                    fontSize = 20.sp,
                )
                TextButton(
                    onClick = {
                        scope.launch {
                            appState.event.emit(ApplicationEvent(Action.NewFile))
                        }
                    },
                    content = {
                        Text("Create a new file (CTRL + N)")
                    }
                )
            }
        }
    }

    if (appState.file != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            /**
             * Editor Text Field
             */
            Box(modifier = Modifier.fillMaxSize().padding(bottom = 30.dp)) {
                ContextMenuArea(
                    items = {
                        listOf(
                            ContextMenuItem(
                                label = "Copy",
                                onClick = {
                                    clipboard.setText(AnnotatedString(viewModel.editorState.text.substring(viewModel.editorState.selection)))
                                },
                            ),
                            ContextMenuItem(
                                label = "Select All",
                                onClick = {
                                    viewModel.editorState.edit {
                                        selectAll()
                                    }
                                },
                            ),
                        )
                    },
                    content = {
                        BasicTextField(
                            modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState),
                            state = viewModel.editorState,
                            textStyle = LocalTextStyle.current.copy(
                                color = MaterialTheme.colors.onSurface.copy(0f),
                                fontSize = appState.editorFontSize.sp,
                                lineHeight = (appState.editorFontSize * 1.75f).sp,
                                fontFamily = FontFamily.Monospace,
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colors.primary),
                            onTextLayout = {
                                viewModel.textLayoutResult = it.invoke()
                            },
                            decorator = { innerTextField ->
                                Text(
                                    text = annotatedString,
                                    color = MaterialTheme.colors.primary,
                                    fontSize = appState.editorFontSize.sp,
                                    lineHeight = (appState.editorFontSize * 1.75f).sp,
                                    fontFamily = FontFamily.Monospace,
                                )
                                innerTextField()
                            }
                        )
                    }
                )
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(scrollState),
                    style = LocalScrollbarStyle.current.copy(
                        shape = RectangleShape
                    )
                )
            }
            /**
             * Editor Status Bar
             */
            Box(modifier = Modifier.fillMaxWidth().height(30.dp).align(Alignment.BottomCenter).background(MaterialTheme.colors.background).padding(horizontal = 8.dp, vertical = 2.dp)) {
                Row(modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd), verticalAlignment = Alignment.CenterVertically) {
                    if (!viewModel.isReading) {
                        Text(
                            text = if (viewModel.editorState.selection.length > 0) "${viewModel.editorState.text.length} characters (${viewModel.editorState.selection.length} selected)" else "${viewModel.editorState.text.length} characters",
                            fontSize = 12.sp,
                            maxLines = 1,
                        )
                    } else {
                        Text(
                            text = "Reading File...",
                            fontSize = 12.sp,
                            maxLines = 1,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }
    }
}