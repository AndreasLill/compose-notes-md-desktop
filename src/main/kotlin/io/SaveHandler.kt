package io

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

            file.bufferedReader().use {
                lines = it.readLines()
            }

            lines.forEach {
                val pair = it.split("=", limit = 2)
                val key = pair[0]
                val value = pair[1]

                when(key) {
                    "workspace" -> appState.setWorkspace(value)
                    "file" -> appState.setFile(File(value))
                }
            }

            appState.setTitle("${appState.workspace} - ${appState.file?.name}")
            return appState
        }
        catch (e: Exception) {
            return ApplicationState()
        }
    }
}