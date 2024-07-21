package application.input

import androidx.compose.ui.input.key.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import application.model.ApplicationState
import application.model.Action
import application.model.ApplicationEvent

object ShortcutHandler {
    /**
     * Shortcuts for app key events.
     */
    fun event(scope: CoroutineScope, appState: ApplicationState, keyEvent: KeyEvent): Boolean {
        appState.isCtrlPressed = keyEvent.isCtrlPressed
        when {
            /**
             * Save File - CTRL + S
             */
            keyEvent.isCtrlPressed && keyEvent.isKeyPressed(Key.S) && appState.file != null -> {
                scope.launch {
                    appState.event.emit(ApplicationEvent(Action.SaveFile))
                }
                return true
            }
            /**
             * New File - CTRL + N
             */
            keyEvent.isCtrlPressed && keyEvent.isKeyPressed(Key.N) && appState.workspace != null -> {
                scope.launch {
                    appState.event.emit(ApplicationEvent(Action.NewFile))
                }
                return true
            }
            /**
             * Editor - Increase Font - CTRL + PageUp
             */
            keyEvent.isCtrlPressed && keyEvent.isKeyPressed(Key.PageUp) && appState.file != null -> {
                appState.editorFontSize = appState.editorFontSize.plus(1).coerceIn(6, 64)
                return true
            }
            /**
             * Editor - Decrease Font - CTRL + PageDown
             */
            keyEvent.isCtrlPressed && keyEvent.isKeyPressed(Key.PageDown) && appState.file != null -> {
                appState.editorFontSize = appState.editorFontSize.minus(1).coerceIn(6, 64)
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