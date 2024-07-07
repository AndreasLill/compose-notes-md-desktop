import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
            (appState.workspace != null && appState.file != null) -> "Compose Notes - ${appState.workspace} - ${appState.file?.fileName}"
            (appState.workspace != null && appState.file == null) -> "Compose Notes - ${appState.workspace}"
            else -> "Compose Notes - Select a workspace!"
        }
    }
    val scope = rememberCoroutineScope()

    Window(
        onCloseRequest = {
            SaveHandler.saveState(appState)
            exitApplication()
        },
        title = windowTitle,
        state = appState.windowState,
        onPreviewKeyEvent = {
            ShortcutHandler.event(scope, appState, it)
        },
        content = {
            MaterialTheme(colors = ColorScheme.Default) {
                MainScreen(appState)
            }
        }
    )
}
