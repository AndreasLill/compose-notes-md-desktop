package ui.workspace

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composenotesmd.desktop.composenotesmd.generated.resources.Res
import com.example.composenotesmd.desktop.composenotesmd.generated.resources.description_24dp
import com.example.composenotesmd.desktop.composenotesmd.generated.resources.folder_24dp
import com.example.composenotesmd.desktop.composenotesmd.generated.resources.folder_open_24dp
import org.jetbrains.compose.resources.painterResource
import java.nio.file.Path
import kotlin.io.path.isDirectory

@Composable
fun WorkspaceFile(
    path: Path,
    depth: Int,
    visible: Boolean,
    selected: Boolean,
    selectedFile: Boolean,
    isOpenFolder: Boolean,
    onClick: () -> Unit,
    onOpenInExplorer: () -> Unit,
    onBeginRename: () -> Unit,
    onRename: (String) -> Unit,
    onDelete: () -> Unit
) {
    val isRenaming = remember { mutableStateOf(false) }
    val textField = remember(path) { mutableStateOf(TextFieldValue(path.fileName.toString())) }
    val focusRequester = remember { FocusRequester() }
    if (visible) {
        ContextMenuArea(
            items = {
                listOf(
                    ContextMenuItem(
                        label = "Open In Explorer",
                        onClick = onOpenInExplorer,
                    ),
                    ContextMenuItem(
                        label = "Rename",
                        onClick = {
                            onBeginRename()
                            isRenaming.value = true
                            textField.value = TextFieldValue(textField.value.text, TextRange(0, textField.value.text.length))
                            focusRequester.requestFocus()
                        },
                    ),
                    ContextMenuItem(
                        label = "Delete",
                        onClick = onDelete,
                    ),
                )
            },
            content = {
                Card(
                    modifier = Modifier.fillMaxWidth().height(36.dp).clickable {
                        onClick()
                    },
                    border = BorderStroke(1.dp, if (selected) MaterialTheme.colors.primary.copy(0.5f) else Color.Transparent),
                    backgroundColor = if (selectedFile) MaterialTheme.colors.primary.copy(0.1f) else Color.Transparent,
                    shape = RoundedCornerShape(2.dp),
                    elevation = 0.dp,
                    content = {
                        Row(
                            modifier = Modifier.padding(4.dp).padding(start = (20 * depth).dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size(16.dp),
                                painter = if (path.isDirectory() && !isOpenFolder) painterResource(Res.drawable.folder_24dp) else if (path.isDirectory() && isOpenFolder) painterResource(Res.drawable.folder_open_24dp) else painterResource(Res.drawable.description_24dp),
                                contentDescription = null,
                                tint = if (selectedFile) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
                            )
                            BasicTextField(
                                modifier = Modifier.focusRequester(focusRequester).pointerHoverIcon(if (isRenaming.value) PointerIcon.Text else PointerIcon.Default, true).onFocusChanged {
                                    if (!it.isFocused) {
                                        textField.value = TextFieldValue(path.fileName.toString())
                                        isRenaming.value = false
                                    }
                                }.onPreviewKeyEvent {
                                    if (it.key == Key.Enter && it.type == KeyEventType.KeyUp && isRenaming.value) {
                                        onRename(textField.value.text)
                                        textField.value = TextFieldValue(path.fileName.toString())
                                        isRenaming.value = false
                                    }
                                    false
                                }
                                .then(
                                    if (isRenaming.value) {
                                        Modifier.fillMaxWidth()
                                    } else {
                                        Modifier
                                    }
                                ),
                                readOnly = !isRenaming.value,
                                value = textField.value,
                                onValueChange = { textField.value = it },
                                singleLine = true,
                                cursorBrush = SolidColor(MaterialTheme.colors.primary),
                                textStyle = LocalTextStyle.current.copy(
                                    color = if (selectedFile || isRenaming.value) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                                    fontSize = 13.sp,
                                ),
                            )
                        }
                    }
                )
            }
        )
    }
}