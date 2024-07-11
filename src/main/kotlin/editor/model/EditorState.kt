package editor.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

class EditorState {
    val colorText by mutableStateOf(Color(0xFFFAFAFA))
    val colorHeader by mutableStateOf(Color(0xFF81D4FA))
    val colorList by mutableStateOf(Color(0xFFCE93D8))
    val colorBlockQuote by mutableStateOf(Color(0xFFA5D6A7))
}