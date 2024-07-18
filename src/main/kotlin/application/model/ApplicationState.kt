package application.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.window.WindowState
import io.FileHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import java.nio.file.Path

class ApplicationState {
    var isCtrlPressed by mutableStateOf(false)
    var workspace by mutableStateOf<Path?>(null)
    var workspaceWidth by mutableStateOf(300f)
    var file by mutableStateOf<Path?>(null)
    var fileText by mutableStateOf(TextFieldValue(""))
    var fileOriginalText by mutableStateOf("")
    var editorFontSize by mutableStateOf(14)
    var unsavedChanges by mutableStateOf(false)
    var windowState by mutableStateOf(WindowState())
    val event = MutableSharedFlow<Action>()
    val confirmDialog = ConfirmDialogState()

    fun getWorkspaceShortString(): String {
        workspace?.let {
            val parts = it.toString().split("\\")
            if (parts.size >= 2) {
                return "..\\${parts[parts.size-2]}\\${parts[parts.size-1]}"
            } else {
                return workspace.toString()
            }
        }
        return ""
    }

    fun discardChanges() {
        this.fileText = TextFieldValue(fileOriginalText)
    }

    suspend fun saveChanges(): Boolean {
        file?.let {
            val saved = FileHandler.saveFile(it, fileText.text)
            fileOriginalText = fileText.text
            return saved
        }
        return false
    }
}