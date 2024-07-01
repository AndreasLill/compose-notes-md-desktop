package model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class DirectoryState(workspace: String) {
    val directory = flow<List<File>> {
        while (workspace.isNotBlank()) {
            val temp = File(workspace).listFiles()?.filter { it.extension == "md" }
            temp?.let {
                emit(it)
            }
            delay(1000)
            println("$workspace polled.")
        }
    }.flowOn(Dispatchers.IO)
}