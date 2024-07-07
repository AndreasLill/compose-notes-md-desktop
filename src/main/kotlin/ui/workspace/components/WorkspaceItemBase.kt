package ui.workspace.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composenotesmd.desktop.composenotesmd.generated.resources.Res
import com.example.composenotesmd.desktop.composenotesmd.generated.resources.description_24dp
import org.jetbrains.compose.resources.painterResource

@Composable
fun WorkspaceItemBase(
    text: String,
    focusRequester: FocusRequester,
    selected: Boolean,
    clickable: Boolean,
    showEdit: Boolean,
    showText: Boolean,
    onKeyEnter: (String) -> Unit,
    onKeyEsc: () -> Unit,
    onClick: () -> Unit,
) {
    val textFieldValue = remember { mutableStateOf(TextFieldValue("")) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp).clickable(clickable) {
            onClick()
        },
        backgroundColor = if (selected) MaterialTheme.colors.primary.copy(0.10f) else Color.Transparent,
        shape = RoundedCornerShape(2.dp),
        elevation = 0.dp,
        content = {
            Row(
                modifier = Modifier.padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(Res.drawable.description_24dp),
                    tint = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                    contentDescription = null
                )
                if (showEdit) {
                    BasicTextField(
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester).onPreviewKeyEvent {
                            if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                                onKeyEnter(textFieldValue.value.text)
                                true
                            } else if (it.key == Key.Escape && it.type == KeyEventType.KeyDown) {
                                onKeyEsc()
                                true
                            } else false
                        },
                        value = textFieldValue.value,
                        onValueChange = { textFieldValue.value = it },
                        textStyle = LocalTextStyle.current.copy(
                            color = MaterialTheme.colors.primary,
                            fontSize = 13.sp,
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colors.primary),
                        singleLine = true,
                        maxLines = 1,
                    )
                }
                if (showText) {
                    Text(
                        text = text,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                    )
                }
            }
        }
    )
}