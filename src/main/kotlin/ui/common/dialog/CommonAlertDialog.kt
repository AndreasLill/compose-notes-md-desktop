package ui.common.dialog

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable

@Composable
fun CommonAlertDialog(
    show: Boolean,
    title: String,
    text: String,
    confirmButton: String,
    cancelButton: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onCancel,
            confirmButton = {
                TextButton(
                    onClick = onConfirm,
                    content = {
                        Text(confirmButton)
                    }
                )
            },
            dismissButton = {
                TextButton(
                    onClick = onCancel,
                    content = {
                        Text(cancelButton)
                    }
                )
            },
            title = {
                Text(title)
            },
            text = {
                Text(text)
            }
        )
    }
}