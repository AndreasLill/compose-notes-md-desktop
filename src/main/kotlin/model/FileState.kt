package model

import java.io.File
import kotlin.io.path.Path

class FileState {
    fun readFile(workspace: String, file: String): String {
        return File(Path(workspace, file).toUri()).bufferedReader().readText()
    }
}