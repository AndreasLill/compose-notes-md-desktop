package application.io

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import application.model.ApplicationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object SaveHandler {
    private val HOME = System.getProperty("user.home")
    private val PATH = "$HOME/.config/ComposeNotes/app.conf"

    fun saveState(appState: ApplicationState) = runBlocking {
        withContext(Dispatchers.IO) {
            val path = Paths.get(PATH)
            if (!Files.exists(path)) {
                Files.createDirectories(path.parent)
                Files.createFile(path)
            }

            val builder = StringBuilder()
            builder.appendLine("editor_font_size=${appState.editorFontSize}")
            builder.appendLine("workspace_width=${appState.workspaceWidth.toInt()}")
            builder.appendLine("window_width=${appState.windowState.size.width.value.toInt()}")
            builder.appendLine("window_height=${appState.windowState.size.height.value.toInt()}")
            builder.appendLine("window_pos_x=${appState.windowState.position.x.value.toInt()}")
            builder.appendLine("window_pos_y=${appState.windowState.position.y.value.toInt()}")
            appState.workspace?.let { builder.appendLine("workspace=$it") }
            appState.file?.let { builder.appendLine("file=$it") }

            Files.writeString(path, builder.toString())
            return@withContext null
        }
    }

    fun loadState(args: Array<String>): ApplicationState {
        try {
            val appState = ApplicationState()
            val path = Paths.get(PATH)

            /**
             * Get path if application was opened with arguments.
             */
            val argPath: Path? = try {
                Paths.get(args[0])
            } catch (ex: Exception) {
                null
            }

            var windowWidth = 1200
            var windowHeight = 900
            var windowPosX = 0
            var windowPosY = 0

            Files.readAllLines(path).forEach { line ->
                val pair = line.split("=", limit = 2)
                val key = pair[0]
                val value = pair[1]

                when(key) {
                    "editor_font_size" -> appState.editorFontSize = value.toInt()
                    "workspace" -> {
                        Paths.get(value).let { path ->
                            if (Files.exists(path)) {
                                appState.workspace = path
                            }
                        }
                    }
                    "workspace_width" -> appState.workspaceWidth = value.toFloat()
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

            /**
             * If path arguments for a file was found, disable workspaces and use as a regular text editor.
             */
            if (argPath != null) {
                appState.workspaceEnabled = false
                appState.file = argPath
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