package com.novachat.app.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.novachat.app.presentation.ui.theme.NovaChatColors
import com.novachat.app.presentation.ui.theme.NovaChatTypography

/**
 * Attachment type entries shown in the bottom sheet grid.
 */
private data class AttachmentOption(
    val label: String,
    val icon: ImageVector,
    val backgroundColor: Color
)

// Fixed: 6 items (2 columns × 3 rows) — added Audio and Poll
private val attachmentOptions = listOf(
    AttachmentOption("Gallery",  Icons.Default.Image,       NovaChatColors.Primary),
    AttachmentOption("Camera",   Icons.Default.CameraAlt,   NovaChatColors.Accent),
    AttachmentOption("Audio",    Icons.Default.AudioFile,   NovaChatColors.Online),
    AttachmentOption("File",     Icons.Default.Description, NovaChatColors.PrimaryContainer),
    AttachmentOption("Location", Icons.Default.LocationOn,  Color(0xFFE67E22)),
    AttachmentOption("Poll",     Icons.Default.BarChart,    Color(0xFF9B59B6))
)

/**
 * Telegram-style attachment launcher bottom sheet.
 *
 * Displays a 2-column × 3-row grid of colorful circular icon buttons.
 * Each button triggers the appropriate action callback and dismisses the sheet.
 *
 * @param onDismiss        Called when the sheet is dismissed by back gesture or scrim tap.
 * @param onGallery        Launches the system image picker.
 * @param onCamera         Launches the system camera.
 * @param onAudio          Launches the system audio picker.
 * @param onFile           Launches the system file picker.
 * @param onLocation       Opens a location picker flow.
 * @param onPoll           Opens the poll creation flow.
 * @param sheetState       Externally controlled [SheetState] for animation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentBottomSheet(
    onDismiss: () -> Unit,
    onGallery: () -> Unit,
    onCamera: () -> Unit,
    onAudio: () -> Unit,
    onFile: () -> Unit,
    onLocation: () -> Unit,
    onPoll: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NovaChatColors.Surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(NovaChatColors.Divider)
            )
        }
    ) {
        Text(
            text = "Share",
            style = NovaChatTypography.TitleMedium,
            color = NovaChatColors.TextPrimary,
            modifier = Modifier.padding(start = 20.dp, bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            items(attachmentOptions) { option ->
                val action: () -> Unit = when (option.label) {
                    "Gallery"  -> { { onGallery(); onDismiss() } }
                    "Camera"   -> { { onCamera(); onDismiss() } }
                    "Audio"    -> { { onAudio(); onDismiss() } }
                    "File"     -> { { onFile(); onDismiss() } }
                    "Location" -> { { onLocation(); onDismiss() } }
                    "Poll"     -> { { onPoll(); onDismiss() } }
                    else       -> { { onDismiss() } }
                }
                AttachmentOptionItem(option = option, onClick = action)
            }
        }
    }
}

@Composable
private fun AttachmentOptionItem(
    option: AttachmentOption,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(option.backgroundColor)
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = option.label,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
        Text(
            text = option.label,
            style = NovaChatTypography.LabelSmall,
            color = NovaChatColors.TextSecondary
        )
    }
}
