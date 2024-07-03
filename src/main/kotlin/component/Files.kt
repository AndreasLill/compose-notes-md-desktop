package component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import model.ApplicationState
import model.DirectoryState

@Composable
fun Files(modifier: Modifier, appState: ApplicationState)  {
    val state = remember(appState.workspace) { DirectoryState(appState.workspace) }
    val directory = state.directory.collectAsState(null)

    Column(modifier = modifier) {
        directory.value?.forEach {
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    appState.file = it
                    appState.title = "${appState.workspace} - ${appState.file?.name}"
                },
                content = {
                    Text(it.name)
                },
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = if (appState.file == it) MaterialTheme.colors.primary.copy(alpha = 0.20f) else Color.Transparent
                )
            )
        }
    }
}