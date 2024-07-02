import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import model.Action
import model.ApplicationState
import screen.MainScreen
import theme.ColorScheme

fun main() = application {
    val appState = remember { ApplicationState() }
    Window(onCloseRequest = ::exitApplication, title = appState.title, state = rememberWindowState(width = Dp(1200f), height = Dp(900f))) {
        MaterialTheme(colors = ColorScheme.Default) {
            MenuBar {
                Menu("File", mnemonic = 'F') {
                    Item("Save", onClick = { appState.setAction(Action.Save) }, shortcut = KeyShortcut(Key.S, ctrl = true))
                }
            }
            MainScreen(appState)
        }
    }
}
