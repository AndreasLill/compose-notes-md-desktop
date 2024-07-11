package editor.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle

class EditorState {
    private val colorText by mutableStateOf(Color(0xFFFAFAFA))
    private val colorHeader by mutableStateOf(Color(0xFF81D4FA))
    private val colorList by mutableStateOf(Color(0xFFCE93D8))
    private val colorBlockQuote by mutableStateOf(Color(0xFFA5D6A7))

    /**
     * Translates markdown source text to annotated string format used by text field.
     */
    fun getMarkdownAnnotatedString(text: String): AnnotatedString {
        val builder = AnnotatedString.Builder()
        val lines = text.lines()

        lines.forEachIndexed { index, line ->
            when {
                line.startsWith("#") -> {
                    buildLine(builder, SpanStyle(colorHeader), line, !isLastLine(index, lines.size))
                }
                line.startsWith("*") -> {
                    buildLine(builder, SpanStyle(colorList), line.substring(0, 1))
                    buildLine(builder, SpanStyle(colorText), line.substring(1, line.length), !isLastLine(index, lines.size))
                }
                line.startsWith(">") -> {
                    buildLine(builder, SpanStyle(colorBlockQuote), line.substring(0, 1))
                    buildLine(builder, SpanStyle(colorText), line.substring(1, line.length), !isLastLine(index, lines.size))
                }
                else -> {
                    buildLine(builder, SpanStyle(colorText), line, !isLastLine(index, lines.size))
                }
            }
        }

        return builder.toAnnotatedString()
    }

    /**
     * Builds annotated string with provided style and handles line breaks.
     */
    private fun buildLine(builder: AnnotatedString.Builder, style: SpanStyle, text: String, breakLine: Boolean = false) {
        builder.withStyle(style) {
            append(text)

            if (breakLine)
                append("\n")
        }
    }

    /**
     * Checks if the current index is the last.
     */
    private fun isLastLine(index: Int, size: Int): Boolean {
        return index == size - 1
    }
}