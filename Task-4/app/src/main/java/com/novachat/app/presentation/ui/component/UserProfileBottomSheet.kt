package com.novachat.app.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.novachat.app.domain.model.User
import com.novachat.app.presentation.ui.theme.NovaChatColors
import com.novachat.app.presentation.ui.theme.NovaChatTypography
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Bottom sheet displaying another user's profile details.
 *
 * @param user The user whose profile is being viewed.
 * @param onDismiss Called when the sheet is closed.
 * @param onStartChat Optional callback to trigger messaging this user.
 * @param sheetState State for the ModalBottomSheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileBottomSheet(
    user: User,
    onDismiss: () -> Unit,
    onStartChat: (() -> Unit)? = null,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var showFullPhoto by remember { mutableStateOf(false) }

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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Close button at top right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = NovaChatColors.TextSecondary
                    )
                }
            }

            // Avatar with online dot
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clickable {
                        if (!user.photoUrl.isNullOrEmpty()) {
                            showFullPhoto = true
                        }
                    }
            ) {
                if (!user.photoUrl.isNullOrEmpty()) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user.photoUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "${user.displayName} profile photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape),
                        loading = {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .background(NovaChatColors.Primary)
                            ) {
                                Text(
                                    text = user.displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                    color = NovaChatColors.TextOnPrimary,
                                    style = NovaChatTypography.HeadlineLarge.copy(fontSize = 38.sp)
                                )
                            }
                        },
                        error = {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .background(NovaChatColors.Primary)
                            ) {
                                Text(
                                    text = user.displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                    color = NovaChatColors.TextOnPrimary,
                                    style = NovaChatTypography.HeadlineLarge.copy(fontSize = 38.sp)
                                )
                            }
                        }
                    )
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(NovaChatColors.Primary)
                    ) {
                        Text(
                            text = user.displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            color = NovaChatColors.TextOnPrimary,
                            style = NovaChatTypography.HeadlineLarge.copy(fontSize = 38.sp)
                        )
                    }
                }

                if (user.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(NovaChatColors.Surface)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(NovaChatColors.Online)
                            .align(Alignment.BottomEnd)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display Name
            Text(
                text = user.displayName.ifBlank { "NovaChat User" },
                color = NovaChatColors.TextPrimary,
                style = NovaChatTypography.HeadlineLarge,
                fontWeight = FontWeight.Bold
            )

            // Status (Online / Last seen)
            val statusText = if (user.isOnline) {
                "Online"
            } else if (user.lastSeen > 0L) {
                "Last seen ${formatLastSeenTime(user.lastSeen)}"
            } else {
                "Offline"
            }

            Text(
                text = statusText,
                color = if (user.isOnline) NovaChatColors.Online else NovaChatColors.TextSecondary,
                style = NovaChatTypography.BodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Info Card (Email & Bio)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NovaChatColors.InputBackground),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    user.email?.takeIf { it.isNotBlank() }?.let { emailText ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(NovaChatColors.Primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email",
                                    tint = NovaChatColors.Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Email",
                                    style = NovaChatTypography.LabelSmall,
                                    color = NovaChatColors.TextSecondary
                                )
                                Text(
                                    text = emailText,
                                    style = NovaChatTypography.BodyMedium,
                                    color = NovaChatColors.TextPrimary
                                )
                            }
                        }
                    }

                    // Bio item
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NovaChatColors.Primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "About",
                                tint = NovaChatColors.Primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "About",
                                style = NovaChatTypography.LabelSmall,
                                color = NovaChatColors.TextSecondary
                            )
                            Text(
                                text = user.bio.ifBlank { "Hey there! I am using NovaChat." },
                                style = NovaChatTypography.BodyMedium,
                                color = NovaChatColors.TextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Button: Send Message
            if (onStartChat != null) {
                Button(
                    onClick = {
                        onStartChat()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NovaChatColors.Primary,
                        contentColor = NovaChatColors.TextOnPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Send Message",
                        style = NovaChatTypography.TitleMedium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Full photo modal preview
    if (showFullPhoto && !user.photoUrl.isNullOrEmpty()) {
        Dialog(onDismissRequest = { showFullPhoto = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
            ) {
                SubcomposeAsyncImage(
                    model = user.photoUrl,
                    contentDescription = "Full profile picture",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                IconButton(
                    onClick = { showFullPhoto = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close full view",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

private fun formatLastSeenTime(timestamp: Long): String {
    if (timestamp <= 0L) return "recently"
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "just now"
        diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m ago"
        diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}h ago"
        else -> SimpleDateFormat("MMM d 'at' HH:mm", Locale.getDefault()).format(Date(timestamp))
    }
}
