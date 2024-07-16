package editor.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import application.model.Action
import application.model.ApplicationState
import editor.model.EditorViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Editor(appState: ApplicationState) {
    val viewModel = remember { EditorViewModel(appState) }
    val scope = rememberCoroutineScope()
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val annotatedString = remember(appState.fileText.text) { viewModel.getMarkdownAnnotatedString(appState.fileText.text) }
    val pointerIcon = remember { mutableStateOf(PointerIcon.Default) }
    val scrollState = rememberScrollState()

    LaunchedEffect(appState.file) {
        if (appState.file == null)
            return@LaunchedEffect

        viewModel.showTextField = false
        viewModel.readFile(appState.file)

        /**
         * Tiny delay required to recreate the composable by hiding and showing, thus clearing the undo queue in text field.
         * Maybe there is a better way to clear it in a future compose version?
         */
        delay(1)
        viewModel.showTextField = true
    }

    LaunchedEffect(appState.fileOriginalText, appState.fileText) {
        viewModel.updateUnsavedChanges()
    }

    LaunchedEffect(Unit) {
        appState.event.collect { event ->
            if (event == Action.SaveFile) {
                appState.saveChanges()
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
                            appState.event.emit(Action.NewFile)
                        }
                    },
                    content = {
                        Text("Create a new file (CTRL + N)")
                    }
                )
            }
        }
    }

    if (appState.workspace != null && appState.file != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (viewModel.showTextField) {
                Box(modifier = Modifier.fillMaxSize().padding(bottom = 30.dp)) {
                    BasicTextField(
                        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState),
                        value = appState.fileText,
                        onValueChange = { appState.fileText = it },
                        textStyle = LocalTextStyle.current.copy(
                            color = MaterialTheme.colors.primary,
                            fontSize = appState.editorFontSize.sp,
                            lineHeight = (appState.editorFontSize * 1.75f).sp,
                            fontFamily = FontFamily.Monospace,
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colors.primary),
                        visualTransformation = {
                            TransformedText(
                                text = annotatedString,
                                offsetMapping = OffsetMapping.Identity
                            )
                        },
                        onTextLayout = {
                            layoutResult.value = it
                        },
                        decorationBox = { innerTextField ->
                            /**
                             * Pointer input capture box for catching annotated urls.
                             * Only active when CTRL is pressed in application.
                             */
                            if (appState.isCtrlPressed) {
                                Box(modifier = Modifier.pointerHoverIcon(pointerIcon.value).onPointerEvent(PointerEventType.Move) { event ->
                                    layoutResult.value?.let { layout ->
                                        val position = layout.getOffsetForPosition(event.changes.first().position)
                                        val annotation = annotatedString.getStringAnnotations(position, position).firstOrNull()
                                        if (annotation?.tag == "URL") {
                                            pointerIcon.value = PointerIcon.Hand
                                        } else {
                                            pointerIcon.value = PointerIcon.Default
                                        }
                                    }
                                }.pointerInput(Unit) {
                                    detectTapGestures { offset ->
                                        layoutResult.value?.let { layout ->
                                            val position = layout.getOffsetForPosition(offset)
                                            annotatedString.getStringAnnotations(position, position).firstOrNull()?.let { annotation ->
                                                if (annotation.tag == "URL") {
                                                    viewModel.openInBrowser(annotation.item)
                                                    appState.isCtrlPressed = false
                                                }
                                            }
                                        }
                                    }
                                })
                            }
                            innerTextField()
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
            }
            Box(modifier = Modifier.fillMaxWidth().height(30.dp).align(Alignment.BottomCenter).background(MaterialTheme.colors.background).padding(horizontal = 8.dp, vertical = 2.dp)) {
                Row(modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)) {
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