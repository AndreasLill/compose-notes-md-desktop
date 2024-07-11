package application.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ConfirmDialogState {
    var show by mutableStateOf(false)
    var title by mutableStateOf("")
    var body by mutableStateOf("")
    var buttonCancel by mutableStateOf("")
    var buttonDiscard by mutableStateOf<String?>(null)
    var buttonConfirm by mutableStateOf("")
    var listenerOnCancel: () -> Unit = {}
    var listenerOnDiscard: () -> Unit = {}
    var listenerOnConfirm: () -> Unit = {}

    fun showDialog(title: String, body: String, buttonCancel: String, buttonDiscard: String? = null, buttonConfirm: String, onCancel: () -> Unit = {}, onDiscard: () -> Unit = {}, onConfirm: () -> Unit) {
        this.show = true
        this.title = title
        this.body = body
        this.buttonCancel = buttonCancel
        this.buttonDiscard = buttonDiscard
        this.buttonConfirm = buttonConfirm
        this.listenerOnCancel = onCancel
        this.listenerOnDiscard = onDiscard
        this.listenerOnConfirm = onConfirm
    }

    fun closeDialog() {
        this.show = false
        this.title = ""
        this.body = ""
        this.buttonCancel = ""
        this.buttonDiscard = ""
        this.buttonConfirm = ""
        this.listenerOnCancel = {}
        this.listenerOnDiscard = {}
        this.listenerOnConfirm = {}
    }
}