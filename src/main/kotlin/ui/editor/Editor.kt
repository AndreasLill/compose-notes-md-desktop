package ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import kotlinx.coroutines.runBlocking
import model.ApplicationState
import model.EditorState
import model.enums.Action
import java.nio.file.Path

@Composable
fun Editor(appState: ApplicationState) {
    val state = remember { EditorState() }
    val originalText = remember(appState.file) { mutableStateOf(readFile(appState.file)) }
    val text = remember(originalText.value) { mutableStateOf(TextFieldValue(originalText.value)) }
    val diff = remember(originalText.value, text.value) { derivedStateOf { originalText.value != text.value.text } }
    val scope = rememberCoroutineScope()

    LaunchedEffect(appState.file) {
        appState.event.collect { event ->
            if (event == Action.SaveFile) {
                appState.file?.let { file ->
                    FileHandler.saveFile(file, text.value.text)
                    originalText.value = text.value.text
                }
            }
        }
    }

    LaunchedEffect(diff.value) {
        if (diff.value) {
            // TODO: Set * for unsaved.
            //appState.title = appState.title.plus("*")
        }
        else {
            // TODO: Remove * for unsaved.
            //appState.title = appState.title.removeSuffix("*")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (appState.file != null) {
            BasicTextField(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                value = text.value,
                onValueChange = { text.value = it },
                textStyle = LocalTextStyle.current.copy(
                    color = MaterialTheme.colors.primary,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                ),
                cursorBrush = SolidColor(MaterialTheme.colors.primary),
                visualTransformation = {
                    TransformedText(
                        text = markdownAnnotatedString(state, text.value.text),
                        offsetMapping = OffsetMapping.Identity
                    )
                }
            )
        } else {
            Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "No file is open",
                    fontSize = 24.sp,
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
        Column(modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart).background(MaterialTheme.colors.background)) {
            Divider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp
            )
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)) {
                Column(modifier = Modifier.align(Alignment.CenterEnd)) {
                    SelectionContainer {
                        Text(
                            text = if (text.value.selection.length > 0) "${text.value.text.length} characters (${text.value.selection.length} selected)" else "${text.value.text.length} characters",
                            fontSize = 12.sp,
                            maxLines = 1,
                        )
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

fun readFile(path: Path?): String = runBlocking {
    path?.let {
        return@runBlocking FileHandler.readFile(it) ?: ""
    }
    return@runBlocking ""
}