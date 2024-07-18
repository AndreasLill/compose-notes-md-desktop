package application.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Tooltip(text: String, content: @Composable () -> Unit) {
    @OptIn(ExperimentalFoundationApi::class)
    TooltipArea(
        tooltip = {
            Surface(
                modifier = Modifier.shadow(4.dp),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(0.5.dp, MaterialTheme.colors.onSurface.copy(0.3f)),
                content = {
                    Box(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
                        Text(
                            text = text,
                            fontSize = 13.sp
                        )
                    }
                }
            )
        },
        delayMillis = 600,
        content = content
    )
}