package component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import io.FileHandler
import model.Action
import model.ApplicationState

@Composable
fun Editor(appState: ApplicationState) {
    val originalText = remember(appState.file) { mutableStateOf(FileHandler.readFile(appState.file)) }
    val text = remember(originalText.value) { mutableStateOf(originalText.value) }
    val diff = remember(text.value) { derivedStateOf { originalText.value != text.value } }

    LaunchedEffect(diff.value) {
        if (diff.value) {
            appState.setTitle(appState.title.plus("*"))
        }
        else {
            appState.setTitle(appState.title.removeSuffix("*"))
        }
    }

    LaunchedEffect(appState.action) {
        if (appState.action == Action.Save) {
            FileHandler.saveFile(appState.file, text.value)
            appState.setAction(Action.None)
            appState.setTitle(appState.title.removeSuffix("*"))
        }
    }

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