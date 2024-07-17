package io

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import application.model.ApplicationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Paths

object SaveHandler {
    private val HOME = System.getProperty("user.home")
    private val PATH = "$HOME/.config/ComposeNotes/app.conf"

    fun saveState(appState: ApplicationState) = runBlocking {
        withContext(Dispatchers.IO) {

            val builder = StringBuilder()
            builder.appendLine("editor_font_size=${appState.editorFontSize}")
            appState.workspace?.let {
                builder.appendLine("workspace=$it")
            }
            appState.file?.let {
                builder.appendLine("file=$it")
            }
            appState.windowState.let {
                builder.appendLine("window_width=${it.size.width.value.toInt()}")
                builder.appendLine("window_height=${it.size.height.value.toInt()}")
                builder.appendLine("window_pos_x=${it.position.x.value.toInt()}")
                builder.appendLine("window_pos_y=${it.position.y.value.toInt()}")
            }

            val path = Paths.get(PATH)

            if (!Files.exists(path)) {
                Files.createDirectories(path.parent)
                Files.createFile(path)
            }

            Files.writeString(path, builder.toString())
        }
    }

    fun loadState(): ApplicationState {
        try {
            val appState = ApplicationState()
            val path = Paths.get(PATH)

            var windowWidth = 1200
            var windowHeight = 900
            var windowPosX = 0
            var windowPosY = 0

            Files.readAllLines(path).forEach { line ->
                val pair = line.split("=", limit = 2)
                val key = pair[0]
                val value = pair[1]

                when(key) {
                    "editor_font_size" -> {
                        appState.editorFontSize = value.toInt()
                    }
                    "workspace" -> {
                        Paths.get(value).let { path ->
                            if (Files.exists(path)) {
                                appState.workspace = path
                            }
                        }
                    }
                    "file" -> {
                        Paths.get(value).let { path ->
                            if (Files.exists(path)) {
                                appState.file = path
                            }
                        }
                    }
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
            return appState
        }
        catch (e: Exception) {
            return ApplicationState()
        }
    }
}