package ui.workspace

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.delay
import model.ApplicationState
import model.enums.Action
import ui.workspace.components.WorkspaceItemFileCreate
import ui.workspace.components.WorkspaceItemFile
import java.io.File

@Composable
fun WorkspaceFileViewer(appState: ApplicationState)  {
    val directory = remember { mutableStateOf<List<File>?>(null) }
    val refreshPoll = remember(appState.workspace) { mutableStateOf(false) }
    val isCreatingFile = remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(refreshPoll.value) {
        if (refreshPoll.value) {
            refreshPoll.value = false
            return@LaunchedEffect
        }

        println("Workspace polling started.")
        while (true) {
            val temp = appState.workspace?.listFiles()?.filter { it.extension == "md" }?.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
            temp?.let {
                if (directory.value != it) {
                    directory.value = it
                    println("${appState.workspace} updated.")
                }
            }
            println("${appState.workspace} polled.")
            delay(1000)
        }
    }

    LaunchedEffect(appState.workspace) {
        appState.event.collect {
            if (it == Action.NewFile) {
                isCreatingFile.value = true
                try {
                    delay(100)
                    focusRequester.requestFocus()
                } catch(_: Exception) {}
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (isCreatingFile.value) {
            WorkspaceItemFileCreate(
                appState = appState,
                focusRequester = focusRequester,
                onRefreshPoll = {
                    refreshPoll.value = true
                },
                onCreateFile = {
                    isCreatingFile.value = it
                }
            )
        }

        directory.value?.forEach { file ->
            WorkspaceItemFile(
                appState = appState,
                file = file,
                isCreatingFile = isCreatingFile.value,
                onRefreshPoll = {
                    refreshPoll.value = true
                }
            )
        }
    }
}