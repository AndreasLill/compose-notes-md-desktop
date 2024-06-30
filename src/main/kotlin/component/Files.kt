package component

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import java.io.File

@Composable
fun Files(modifier: Modifier, workspace: String)  {
    val files = remember(workspace) { derivedStateOf { File(workspace).listFiles()?.filter { it.extension == "md" } } }
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        files.value?.forEach { file ->
            Text(file.name)
        }
    }
}