import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import input.ShortcutHandler
import io.SaveHandler
import ui.screen.MainScreen
import theme.ColorScheme

fun main() = application {
    val appState = remember { SaveHandler.loadState() }
    val windowTitle = remember(appState.workspace, appState.file) {
        when {
            (appState.workspace.isNotBlank() && appState.file != null) -> "Compose Notes - ${appState.workspace} - ${appState.file?.name}"
            (appState.workspace.isNotBlank() && appState.file == null) -> "Compose Notes - ${appState.workspace}"
            else -> "Compose Notes - Select a workspace!"
        }
    }

    Window(
        onCloseRequest = {
            SaveHandler.saveState(appState)
            exitApplication()
        },
        title = windowTitle,
        state = appState.windowState,
        onPreviewKeyEvent = {
            ShortcutHandler.event(appState, it)
        },
        content = {
            MaterialTheme(colors = ColorScheme.Default) {
                MainScreen(appState)
            }
        }
    )
}
