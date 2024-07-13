package application.input

import androidx.compose.ui.input.key.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import application.model.ApplicationState
import application.model.Action

object ShortcutHandler {
    /**
     * Shortcuts for app key events.
     */
    fun event(scope: CoroutineScope, appState: ApplicationState, keyEvent: KeyEvent): Boolean {
        appState.isCtrlPressed = keyEvent.isCtrlPressed
        when {
            keyEvent.isCtrlPressed && keyEvent.isKeyPressed(Key.S) && appState.file != null -> {
                scope.launch {
                    appState.event.emit(Action.SaveFile)
                }
                return true
            }
            keyEvent.isCtrlPressed && keyEvent.isKeyPressed(Key.N) && appState.workspace != null -> {
                scope.launch {
                    appState.event.emit(Action.NewFile)
                }
                return true
            }
            else -> return false
        }
    }

    /**
     * Extension function of KeyEvent.
     */
    private fun KeyEvent.isKeyPressed(key: Key): Boolean {
        return this.key == key && this.type == KeyEventType.KeyDown
    }
}