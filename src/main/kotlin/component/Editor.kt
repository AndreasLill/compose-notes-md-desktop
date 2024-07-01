package component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import model.FileState

@Composable
fun Editor(workspace: String, file: String) {
    val state = remember { FileState() }
    val text = remember(file) { mutableStateOf(state.readFile(workspace, file)) }
    BasicTextField(
        modifier = Modifier.fillMaxSize(),
        value = text.value,
        onValueChange = { text.value = it },
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colors.primary,
        ),
        cursorBrush = SolidColor(MaterialTheme.colors.primary)
    )
}