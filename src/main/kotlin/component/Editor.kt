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
    val text = remember(appState.file) { mutableStateOf(FileHandler.readFile(appState.file)) }

    LaunchedEffect(appState.action) {
        if (appState.action == Action.Save) {
            FileHandler.saveFile(appState.file, text.value)
            appState.setAction(Action.None)
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