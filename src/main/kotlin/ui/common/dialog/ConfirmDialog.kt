package ui.common.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmDialog(
    show: Boolean,
    title: String,
    body: String,
    cancelButton: String,
    discardButton: String? = null,
    confirmButton: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    onDiscard: () -> Unit = {}
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onCancel,
            buttons = {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
                    TextButton(
                        onClick = onCancel,
                        content = {
                            Text(cancelButton)
                        }
                    )
                    discardButton?.let {
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = onDiscard,
                            content = {
                                Text(discardButton)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onConfirm,
                        content = {
                            Text(confirmButton)
                        }
                    )
                }
            },
            title = {
                Text(title)
            },
            text = {
                Text(body)
            }
        )
    }
}