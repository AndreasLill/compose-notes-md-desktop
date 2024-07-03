package io

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import model.ApplicationState
import java.io.File

object SaveHandler {
    private val HOME = System.getProperty("user.home")
    private val PATH = "$HOME/.config/ComposeNotesMD/app.conf"

    fun saveState(appState: ApplicationState) = runBlocking {
        withContext(Dispatchers.IO) {
            val file = File(PATH)
            file.parentFile.mkdirs()
            file.createNewFile()

            val builder = StringBuilder()
            builder.appendLine("workspace=${appState.workspace}")
            builder.appendLine("file=${appState.file}")
            builder.appendLine("window_width=${appState.windowState.size.width.value.toInt()}")
            builder.appendLine("window_height=${appState.windowState.size.height.value.toInt()}")
            builder.appendLine("window_pos_x=${appState.windowState.position.x.value.toInt()}")
            builder.appendLine("window_pos_y=${appState.windowState.position.y.value.toInt()}")

            file.bufferedWriter().use {
                it.write(builder.toString())
            }
            println("Saved state")
        }
    }

    fun loadState(): ApplicationState {
        val file = File(PATH)
        try {
            val appState = ApplicationState()
            val lines: List<String>

            var windowWidth = 1200
            var windowHeight = 900
            var windowPosX = 0
            var windowPosY = 0

            file.bufferedReader().use {
                lines = it.readLines()
            }

            lines.forEach {
                val pair = it.split("=", limit = 2)
                val key = pair[0]
                val value = pair[1]

                when(key) {
                    "workspace" -> appState.workspace = value
                    "file" -> appState.file = File(value)
                    "window_width" -> windowWidth = value.toInt()
                    "window_height" -> windowHeight = value.toInt()
                    "window_pos_x" -> windowPosX = value.toInt()
                    "window_pos_y" -> windowPosY = value.toInt()
                }
            }

            appState.windowState = WindowState(
                size = DpSize(windowWidth.dp, windowHeight.dp),
                position = WindowPosition(windowPosX.dp, windowPosY.dp)
            )
            appState.title = "${appState.workspace} - ${appState.file?.name}"
            return appState
        }
        catch (e: Exception) {
            return ApplicationState()
        }
    }
}