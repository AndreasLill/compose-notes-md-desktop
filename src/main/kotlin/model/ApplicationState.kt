package model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ApplicationState {
    var screen by mutableStateOf(Screens.Main)
    var workspace by mutableStateOf("")
    var file by mutableStateOf("")
    var title by mutableStateOf("No Workspace Selected!")
}