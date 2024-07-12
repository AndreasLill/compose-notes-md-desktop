package editor.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import application.model.ApplicationState
import io.FileHandler
import java.nio.file.Path

class EditorViewModel(private val appState: ApplicationState) {
    private val colorText by mutableStateOf(Color(0xFFFAFAFA))
    private val colorHeader by mutableStateOf(Color(0xFF40C4FF))
    private val colorList by mutableStateOf(Color(0xFFB388FF))
    private val colorBlockQuote by mutableStateOf(Color(0xFF00E676))
    private val colorUrl by mutableStateOf(Color(0xFFFF80AB))
    private val colorItalic by mutableStateOf(Color(0xFF18FFFF))
    private val colorBold by mutableStateOf(Color(0xFF00B8D4))

    companion object {
        private const val REGEX_URL = "https?://\\S+"
        private const val REGEX_ITALIC = "\\*[^*]+\\*"
        private const val REGEX_BOLD = "\\*\\*[^*]+\\*\\*"
        private val REGEX_GROUPS = Regex("($REGEX_URL)|($REGEX_ITALIC)|($REGEX_BOLD)")
    }

    fun updateUnsavedChanges() {
        appState.unsavedChanges = appState.fileOriginalText != appState.fileText.text
    }

    suspend fun readFile(path: Path?) {
        path?.let {
            appState.fileOriginalText = FileHandler.readFile(it) ?: ""
            appState.fileText = TextFieldValue(appState.fileOriginalText)
        }
    }

    /**
     * Translates markdown source text to annotated string format used by text field.
     */
    fun getMarkdownAnnotatedString(text: String): AnnotatedString {
        val builder = AnnotatedString.Builder()
        val lines = text.lines()

        lines.forEachIndexed { index, line ->
            when {
                /**
                 * Header
                 */
                line.startsWith("#") || line.startsWith("##") || line.startsWith("###") || line.startsWith("####") || line.startsWith("#####") || line.startsWith("######") -> {
                    buildLineAnnotations(builder, SpanStyle(colorHeader), line)
                }
                /**
                 * Unordered List
                 */
                line.startsWith("-") || line.startsWith("*") || line.startsWith("+") -> {
                    builder.withStyle(SpanStyle(colorList)) {
                        builder.append(line.substring(0, 1))
                    }
                    buildLineAnnotations(builder, SpanStyle(colorText), line.substring(1))
                }
                /**
                 * Block Quote
                 */
                line.startsWith(">") -> {
                    builder.withStyle(SpanStyle(colorBlockQuote)) {
                        builder.append(line.substring(0, 1))
                    }
                    buildLineAnnotations(builder, SpanStyle(colorText), line.substring(1))
                }
                else -> {
                    buildLineAnnotations(builder, SpanStyle(colorText), line)
                }
            }

            if (index < lines.size - 1)
                builder.append("\n")
        }

        return builder.toAnnotatedString()
    }

    private fun buildLineAnnotations(builder: AnnotatedString.Builder, baseStyle: SpanStyle, line: String) {
        val matches = REGEX_GROUPS.findAll(line).toList().sortedBy { it.range.first }

        if (matches.isNotEmpty()) {
            var lastIndex = 0
            matches.forEach { match ->
                builder.withStyle(baseStyle) {
                    builder.append(line.substring(lastIndex, match.range.first))
                }

                match.groups[1]?.let {
                    // URL GROUP
                    builder.withStyle(SpanStyle(colorUrl)) {
                        builder.append(match.value)
                    }
                }
                match.groups[2]?.let {
                    // ITALIC GROUP
                    builder.withStyle(SpanStyle(colorItalic)) {
                        builder.append(match.value)
                    }
                }
                match.groups[3]?.let {
                    // BOLD GROUP
                    builder.withStyle(SpanStyle(colorBold)) {
                        builder.append(match.value)
                    }
                }

                lastIndex = match.range.last + 1
            }

            builder.withStyle(baseStyle) {
                builder.append(line.substring(lastIndex))
            }
        }

        if (matches.isEmpty()) {
            builder.withStyle(baseStyle) {
                builder.append(line)
            }
        }
    }
}