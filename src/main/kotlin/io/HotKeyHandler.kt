package io

import androidx.compose.ui.input.key.*
import model.Action
import model.ApplicationState

object HotKeyHandler {
    fun event(appState: ApplicationState, keyEvent: KeyEvent): Boolean {
        when {
            keyEvent.isCtrlPressed && keyEvent.key == Key.S && keyEvent.type == KeyEventType.KeyDown && appState.file != null -> {
                appState.setAction(Action.Save)
                return true
            }
            else -> return false
        }
    }
}