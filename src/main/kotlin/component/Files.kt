package component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import model.DirectoryState

@Composable
fun Files(modifier: Modifier, workspace: String, callback: (file: String) -> Unit)  {
    val state = remember(workspace) { DirectoryState(workspace) }
    val directory = state.directory.collectAsState(null)

    Column(modifier = modifier) {
        directory.value?.forEach {
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    callback(it.name)
                },
                content = {
                    Text(it.name)
                }
            )
        }
    }
}