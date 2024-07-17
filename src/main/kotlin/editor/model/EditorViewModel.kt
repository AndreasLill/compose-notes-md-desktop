package editor.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import application.model.ApplicationState
import io.FileHandler
import java.awt.Desktop
import java.net.URI
import java.nio.file.Path

class EditorViewModel(private val appState: ApplicationState) {
    private val colorText by mutableStateOf(Color(0xFFFAFAFA))
    private val colorHeader by mutableStateOf(Color(0xFF40C4FF))
    private val colorList by mutableStateOf(Color(0xFFEA80FC))
    private val colorBlockQuote by mutableStateOf(Color(0xFFB2FF59))
    private val colorUrl by mutableStateOf(Color(0xFFFF80AB))
    private val colorItalic by mutableStateOf(Color(0xFF18FFFF))
    private val colorBold by mutableStateOf(Color(0xFF00B8D4))
    private val colorCode by mutableStateOf(Color(0xFFFFAB40))
    var showTextField by mutableStateOf(false)

    companion object {
        private const val REGEX_URL = "https?://\\S+"
        private const val REGEX_ITALIC = "\\*[\\S]+\\*"
        private const val REGEX_BOLD = "\\*\\*[\\S]+\\*\\*"
        private const val REGEX_CODE = "`[\\S]+`"
        private val REGEX_GROUPS = Regex("($REGEX_URL)|($REGEX_ITALIC)|($REGEX_BOLD)|($REGEX_CODE)")
    }

    suspend fun readFile(path: Path?) {
        path?.let {
            appState.fileOriginalText = FileHandler.readFile(it) ?: ""
            appState.fileText = TextFieldValue(appState.fileOriginalText)
        }
    }

    fun updateUnsavedChanges() {
        appState.unsavedChanges = appState.fileOriginalText != appState.fileText.text
    }

    fun openInBrowser(uri: String) {
        Desktop.getDesktop().browse(URI(uri))
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

                /**
                 * URL Group
                 */
                match.groups[1]?.let {
                    builder.pushStringAnnotation("URL", match.value)
                    builder.withStyle(SpanStyle(colorUrl, textDecoration = TextDecoration.Underline)) {
                        builder.append(match.value)
                    }
                    builder.pop()
                }
                /**
                 * Italic Group
                 */
                match.groups[2]?.let {
                    builder.withStyle(SpanStyle(color = colorItalic, fontStyle = FontStyle.Italic)) {
                        builder.append(match.value)
                    }
                }
                /**
                 * Bold Group
                 */
                match.groups[3]?.let {
                    builder.withStyle(SpanStyle(color = colorBold, fontWeight = FontWeight.Bold)) {
                        builder.append(match.value)
                    }
                }
                /**
                 * Code Group
                 */
                match.groups[4]?.let {
                    builder.withStyle(SpanStyle(color = colorCode, fontWeight = FontWeight.Bold)) {
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