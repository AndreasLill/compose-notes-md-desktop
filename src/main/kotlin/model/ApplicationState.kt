package model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.flow.MutableSharedFlow
import model.enums.Action
import model.enums.Screen
import java.io.File

class ApplicationState {
    var screen by mutableStateOf(Screen.Main)
    var workspace by mutableStateOf<File?>(null)
    var file by mutableStateOf<File?>(null)
    var windowState by mutableStateOf(WindowState())
    val event = MutableSharedFlow<Action>()
}