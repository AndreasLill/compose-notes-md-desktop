import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import input.ShortcutHandler
import io.SaveHandler
import screen.MainScreen
import theme.ColorScheme

fun main() = application {
    val appState = remember { SaveHandler.loadState() }
    Window(
        onCloseRequest = {
            SaveHandler.saveState(appState)
            exitApplication()
        },
        title = appState.title,
        state = appState.windowState,
        onKeyEvent = {
            ShortcutHandler.event(appState, it)
        },
        content = {
            MaterialTheme(colors = ColorScheme.Default) {
                MainScreen(appState)
            }
        }
    )
}
