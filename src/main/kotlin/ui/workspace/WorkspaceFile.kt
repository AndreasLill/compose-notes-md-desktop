package ui.workspace

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composenotesmd.desktop.composenotesmd.generated.resources.Res
import com.example.composenotesmd.desktop.composenotesmd.generated.resources.description_24dp
import com.example.composenotesmd.desktop.composenotesmd.generated.resources.folder_24dp
import com.example.composenotesmd.desktop.composenotesmd.generated.resources.folder_open_24dp
import org.jetbrains.compose.resources.painterResource
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.nameWithoutExtension

@Composable
fun WorkspaceFile(
    path: Path,
    depth: Int,
    visible: Boolean,
    selected: Boolean,
    selectedFile: Boolean,
    isOpenFolder: Boolean,
    onClick: () -> Unit
) {
    if (visible) {
        Card(
            modifier = Modifier.fillMaxWidth().height(36.dp).clickable {
                onClick()
            },
            border = BorderStroke(1.dp, if (selected) MaterialTheme.colors.primary.copy(0.5f) else Color.Transparent),
            backgroundColor = if (selectedFile) MaterialTheme.colors.primary.copy(0.1f) else Color.Transparent,
            shape = RoundedCornerShape(2.dp),
            elevation = 0.dp,
            content = {
                Row(
                    modifier = Modifier.padding(4.dp).padding(start = (20 * depth).dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = if (path.isDirectory() && !isOpenFolder) painterResource(Res.drawable.folder_24dp) else if (path.isDirectory() && isOpenFolder) painterResource(Res.drawable.folder_open_24dp) else painterResource(Res.drawable.description_24dp),
                        contentDescription = null,
                        tint = if (selectedFile) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
                    )
                    Text(
                        text = path.nameWithoutExtension,
                        fontSize = 13.sp,
                        color = if (selectedFile) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
                    )
                }
            }
        )
    }
}