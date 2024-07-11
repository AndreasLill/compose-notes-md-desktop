package ui.editor

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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.FileHandler
import kotlinx.coroutines.launch
import model.ApplicationState
import model.EditorState
import model.enums.Action

@Composable
fun Editor(appState: ApplicationState) {
    val state = remember { EditorState() }
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
                        text = markdownAnnotatedString(state, appState.fileText.text),
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

/**
 * Translates markdown source text to annotated string format used by text field.
 */
fun markdownAnnotatedString(state: EditorState, text: String): AnnotatedString {
    val builder = AnnotatedString.Builder()
    val lines = text.lines()

    lines.forEachIndexed { index, line ->
        when {
            line.startsWith("#") -> {
                buildLine(builder, SpanStyle(state.colorHeader), line, !isLastLine(index, lines.size))
            }
            line.startsWith("*") -> {
                buildLine(builder, SpanStyle(state.colorList), line.substring(0, 1))
                buildLine(builder, SpanStyle(state.colorText), line.substring(1, line.length), !isLastLine(index, lines.size))
            }
            line.startsWith(">") -> {
                buildLine(builder, SpanStyle(state.colorBlockQuote), line.substring(0, 1))
                buildLine(builder, SpanStyle(state.colorText), line.substring(1, line.length), !isLastLine(index, lines.size))
            }
            else -> {
                buildLine(builder, SpanStyle(state.colorText), line, !isLastLine(index, lines.size))
            }
        }
    }

    return builder.toAnnotatedString()
}

/**
 * Builds annotated string with provided style and handles line breaks.
 */
fun buildLine(builder: AnnotatedString.Builder, style: SpanStyle, text: String, breakLine: Boolean = false) {
    builder.withStyle(style) {
        append(text)

        if (breakLine)
            append("\n")
    }
}

/**
 * Checks if the current index is the last.
 */
fun isLastLine(index: Int, size: Int): Boolean {
    return index == size - 1
}