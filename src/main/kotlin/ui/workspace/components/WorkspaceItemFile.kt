package ui.workspace.components

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.FileHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.ApplicationState
import java.awt.Desktop
import java.io.File

@Composable
fun WorkspaceItemFile(appState: ApplicationState, file: File, isCreatingFile: Boolean, onRefreshPoll: () -> Unit) {
    val focusRequester = remember { FocusRequester() }
    val selected = (appState.file == file && !isCreatingFile)
    val renameFileText = remember { mutableStateOf(TextFieldValue("")) }
    val renameFileName = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    ContextMenuArea(
        items = {
            listOf(
                ContextMenuItem("Open In Explorer") {
                    val desktop = Desktop.getDesktop()
                    desktop.open(appState.workspace)
                },
                ContextMenuItem("Rename") {
                    renameFileName.value = file.name
                    renameFileText.value = TextFieldValue(file.nameWithoutExtension, TextRange(0, file.nameWithoutExtension.length))
                    scope.launch {
                        try {
                            delay(100)
                        } catch(_: Exception) {}
                            focusRequester.requestFocus()
                    }
                },
                ContextMenuItem("Delete") {
                    scope.launch {
                        FileHandler.deleteFile(file).let { success ->
                            if (success) {
                                onRefreshPoll()
                            }
                        }
                    }
                },
            )
        },
        content = {
            WorkspaceItemBase(
                text = file.nameWithoutExtension,
                focusRequester = focusRequester,
                selected = selected,
                clickable = true,
                showEdit = file.name == renameFileName.value,
                showText = file.name != renameFileName.value,
                onKeyEnter = { value ->
                    scope.launch {
                        val renamedFile = FileHandler.renameFile(file, value)
                        appState.file = renamedFile
                        renameFileName.value = ""
                        renameFileText.value = TextFieldValue("")
                        onRefreshPoll()
                    }
                },
                onKeyEsc = {
                    renameFileName.value = ""
                    renameFileText.value = TextFieldValue("")
                },
                onClick = {
                    appState.file = file
                }
            )
        }
    )
}