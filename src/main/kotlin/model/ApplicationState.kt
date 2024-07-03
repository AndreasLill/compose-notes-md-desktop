package model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import model.enums.Action
import model.enums.Screen
import java.io.File

class ApplicationState {
    var screen by mutableStateOf(Screen.Main)
        private set
    var workspace by mutableStateOf("")
        private set
    var file by mutableStateOf<File?>(null)
        private set
    var title by mutableStateOf("No Workspace Selected!")
        private set
    var action by mutableStateOf(Action.None)
        private set

    @JvmName("jvmSetScreen")
    fun setScreen(value: Screen) {
        screen = value
    }
    @JvmName("jvmSetWorkspace")
    fun setWorkspace(value: String) {
        workspace = value
    }
    @JvmName("jvmSetFile")
    fun setFile(value: File?) {
        file = value
    }
    @JvmName("jvmSetTitle")
    fun setTitle(value: String) {
        title = value
    }
    @JvmName("jvmSetAction")
    fun setAction(value: Action) {
        action = value
    }
}