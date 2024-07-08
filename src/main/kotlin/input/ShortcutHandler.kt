package input

import androidx.compose.ui.input.key.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import model.ApplicationState
import model.enums.Action

object ShortcutHandler {
    fun event(scope: CoroutineScope, appState: ApplicationState, keyEvent: KeyEvent): Boolean {
        when {
            keyEvent.isCtrlPressed && keyEvent.key == Key.S && keyEvent.type == KeyEventType.KeyDown && appState.file != null -> {
                scope.launch {
                    appState.event.emit(Action.SaveFile)
                }
                return true
            }
            else -> return false
        }
    }
}