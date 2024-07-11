import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import input.ShortcutHandler
import io.SaveHandler
import ui.screen.MainScreen
import theme.ColorScheme
import ui.common.dialog.ConfirmDialog

fun main() = application {
    val appState = remember { SaveHandler.loadState() }
    val windowTitle = remember(appState.workspace, appState.file, appState.unsavedChanges) {
        when {
            (appState.workspace != null && appState.file != null && !appState.unsavedChanges) -> "Compose Notes - ${appState.workspace} - ${appState.file?.fileName}"
            (appState.workspace != null && appState.file != null && appState.unsavedChanges) -> "Compose Notes - ${appState.workspace} - ${appState.file?.fileName}*"
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
                ConfirmDialog(
                    show = appState.confirmDialog.show,
                    title = appState.confirmDialog.title,
                    body = appState.confirmDialog.body,
                    cancelButton = appState.confirmDialog.buttonCancel,
                    discardButton = appState.confirmDialog.buttonDiscard,
                    confirmButton = appState.confirmDialog.buttonConfirm,
                    onCancel = {
                        appState.confirmDialog.listenerOnCancel.invoke()
                        appState.confirmDialog.closeDialog()
                    },
                    onDiscard = {
                        appState.confirmDialog.listenerOnDiscard.invoke()
                        appState.confirmDialog.closeDialog()
                    },
                    onConfirm = {
                        appState.confirmDialog.listenerOnConfirm.invoke()
                        appState.confirmDialog.closeDialog()
                    }
                )
            }
        }
    )
}
