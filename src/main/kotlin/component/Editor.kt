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

@Composable
fun Editor() {
    val text = remember { mutableStateOf("") }
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