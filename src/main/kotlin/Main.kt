import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import input.ShortcutHandler
import model.ApplicationState
import screen.MainScreen
import theme.ColorScheme

fun main() = application {
    val appState = remember { ApplicationState() }
    Window(
        onCloseRequest = ::exitApplication,
        title = appState.title,
        state = rememberWindowState(width = 1200.dp, height = 900.dp),
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
