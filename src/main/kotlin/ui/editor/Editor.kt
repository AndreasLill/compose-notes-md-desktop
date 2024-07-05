package ui.editor

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import io.FileHandler
import kotlinx.coroutines.runBlocking
import model.ApplicationState
import model.EditorState
import model.enums.Action
import java.io.File

@Composable
fun Editor(appState: ApplicationState) {
    val state = remember { EditorState() }
    val originalText = remember(appState.file) {
        mutableStateOf(
            readFile(appState.file)
        )
    }
    val text = remember(originalText.value) { mutableStateOf(originalText.value) }
    val diff = remember(originalText.value, text.value) { derivedStateOf { originalText.value != text.value } }

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

    LaunchedEffect(appState.action) {
        if (appState.action == Action.Save) {
            FileHandler.saveFile(appState.file, text.value)
            appState.action = Action.None
            originalText.value = text.value
        }
    }

    BasicTextField(
        modifier = Modifier.fillMaxSize(),
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
                text = markdownAnnotatedString(state, text.value),
                offsetMapping = OffsetMapping.Identity
            )
        }
    )
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

fun readFile(file: File?): String = runBlocking {
    return@runBlocking FileHandler.readFile(file)
}