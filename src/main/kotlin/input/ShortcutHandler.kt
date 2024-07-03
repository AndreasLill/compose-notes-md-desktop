package input

import androidx.compose.ui.input.key.*
import model.ApplicationState
import model.enums.Action

object ShortcutHandler {
    fun event(appState: ApplicationState, keyEvent: KeyEvent): Boolean {
        when {
            keyEvent.isCtrlPressed && keyEvent.key == Key.S && keyEvent.type == KeyEventType.KeyDown && appState.file != null -> {
                appState.action = Action.Save
                return true
            }
            else -> return false
        }
    }
}