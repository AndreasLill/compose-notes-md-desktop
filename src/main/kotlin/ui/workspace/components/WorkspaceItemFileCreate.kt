package ui.workspace.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.TextFieldValue
import io.FileHandler
import kotlinx.coroutines.launch
import model.ApplicationState

@Composable
fun WorkspaceItemFileCreate(appState: ApplicationState, focusRequester: FocusRequester, onRefreshPoll: () -> Unit, onCreateFile: (Boolean) -> Unit) {
    val createFileText = remember { mutableStateOf(TextFieldValue("")) }
    val scope = rememberCoroutineScope()

    WorkspaceItemBase(
        text = "",
        focusRequester = focusRequester,
        selected = true,
        clickable = false,
        showEdit = true,
        showText = false,
        onKeyEnter = { value ->
            scope.launch {
                appState.file = FileHandler.createFile(appState.workspace, value)
                createFileText.value = TextFieldValue("")
                onCreateFile(false)
                onRefreshPoll()
            }
        },
        onKeyEsc = {
            createFileText.value = TextFieldValue("")
            onCreateFile(false)
        },
        onClick = {}
    )
}