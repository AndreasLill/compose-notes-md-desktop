import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import application.input.ShortcutHandler
import application.screen.MainScreen
import application.theme.ColorScheme
import application.ui.ConfirmDialog
import application.io.SaveHandler
import kotlinx.coroutines.launch

fun main() = application {
    val appState = remember { SaveHandler.loadState() }
    val windowTitle = remember(appState.workspace, appState.file, appState.unsavedChanges) {
        when {
            (appState.file != null && !appState.unsavedChanges) -> "Compose Notes - ${appState.file?.fileName}"
            (appState.file != null && appState.unsavedChanges) -> "Compose Notes - ${appState.file?.fileName}*"
            else -> "Compose Notes"
        }
    }
    val scope = rememberCoroutineScope()

    Window(
        onCloseRequest = {
            if (appState.unsavedChanges) {
                appState.confirmDialog.showDialog(
                    title = "Unsaved Changes",
                    body = "There are unsaved changes in '${appState.file?.fileName}'\nDo you want to save the changes?",
                    buttonCancel = "Cancel",
                    buttonDiscard = "Discard",
                    buttonConfirm = "Save Changes",
                    onDiscard = {
                        appState.discardChanges()
                        SaveHandler.saveState(appState)
                        exitApplication()
                    },
                    onConfirm = {
                        scope.launch {
                            appState.saveChanges()
                            SaveHandler.saveState(appState)
                            exitApplication()
                        }
                    }
                )
            } else {
                SaveHandler.saveState(appState)
                exitApplication()
            }
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
