package com.novachat.app.presentation.profile

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.novachat.app.presentation.ui.theme.NovaChatColors
import com.novachat.app.presentation.ui.theme.NovaChatTypography
import com.novachat.app.presentation.ui.util.AvatarHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onSignedOut: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showPhotoDialog by remember { mutableStateOf(false) }
    var showChangeEmailDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var avatarOptions by remember { mutableStateOf(AvatarHelper.generateRandomAvatars(12)) }

    var editedName by remember(uiState.user?.displayName) { mutableStateOf(uiState.user?.displayName ?: "") }
    var editedBio by remember(uiState.user?.bio) { mutableStateOf(uiState.user?.bio ?: "") }

    var hasNavigatedOut by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.isSignedOut) {
        if (uiState.isSignedOut && !hasNavigatedOut) {
            hasNavigatedOut = true
            onSignedOut()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    // ── Sign-out confirmation dialog ──────────────────────────────────────────────
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Sign Out", style = NovaChatTypography.TitleMedium) },
            text = { Text("Are you sure you want to sign out?", style = NovaChatTypography.BodyMedium) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutDialog = false
                        viewModel.signOut()
                    }
                ) {
                    Text("Sign Out", color = NovaChatColors.Accent, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancel", color = NovaChatColors.TextSecondary)
                }
            },
            containerColor = NovaChatColors.Surface
        )
    }

    // ── Delete Account confirmation dialog ────────────────────────────────────────
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Delete Account", style = NovaChatTypography.TitleMedium, color = Color(0xFFE53935)) },
            text = {
                Text(
                    "Are you sure you want to delete your account? This action is permanent and will completely delete your profile and chats.",
                    style = NovaChatTypography.BodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteAccountDialog = false
                        viewModel.deleteAccount()
                    }
                ) {
                    Text("Delete Account", color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel", color = NovaChatColors.TextSecondary)
                }
            },
            containerColor = NovaChatColors.Surface
        )
    }

    // ── Change Email Dialog ──────────────────────────────────────────────────────
    if (showChangeEmailDialog) {
        var newEmail by remember { mutableStateOf("") }
        var currentPassword by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showChangeEmailDialog = false },
            title = { Text("Change Email", style = NovaChatTypography.TitleMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Current Email: ${uiState.user?.email ?: "None"}",
                        style = NovaChatTypography.BodySmall,
                        color = NovaChatColors.TextSecondary
                    )
                    OutlinedTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        label = { Text("New Email Address") },
                        placeholder = { Text("ayush.sharma@example.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Current Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.changeEmail(newEmail, currentPassword) {
                            showChangeEmailDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NovaChatColors.Primary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = newEmail.isNotBlank() && currentPassword.isNotBlank() && !uiState.isUpdating
                ) {
                    if (uiState.isUpdating) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Update Email")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangeEmailDialog = false }) {
                    Text("Cancel", color = NovaChatColors.TextSecondary)
                }
            },
            containerColor = NovaChatColors.Surface
        )
    }

    // ── Change Password Dialog ──────────────────────────────────────────────────
    if (showChangePasswordDialog) {
        var currentPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var currentPasswordVisible by remember { mutableStateOf(false) }
        var newPasswordVisible by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = { Text("Change Password", style = NovaChatTypography.TitleMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Current Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                                Icon(
                                    imageVector = if (currentPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password (min 6 chars)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                Icon(
                                    imageVector = if (newPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm New Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                    if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && newPassword != confirmPassword) {
                        Text("Passwords do not match", color = Color(0xFFE53935), style = NovaChatTypography.BodySmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.changePassword(currentPassword, newPassword) {
                            showChangePasswordDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NovaChatColors.Primary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = currentPassword.isNotBlank() && newPassword.length >= 6 && newPassword == confirmPassword && !uiState.isUpdating
                ) {
                    if (uiState.isUpdating) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Update Password")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) {
                    Text("Cancel", color = NovaChatColors.TextSecondary)
                }
            },
            containerColor = NovaChatColors.Surface
        )
    }

    // ── Photo options & Avatar selection dialog ──────────────────────────────────
    if (showPhotoDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Select Avatar", style = NovaChatTypography.TitleMedium)
                    TextButton(
                        onClick = {
                            avatarOptions = AvatarHelper.generateRandomAvatars(12)
                        }
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Randomize",
                            modifier = Modifier.size(16.dp),
                            tint = NovaChatColors.Primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Randomize", style = NovaChatTypography.LabelMedium, color = NovaChatColors.Primary)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Tap an avatar to set it as your profile picture:",
                        style = NovaChatTypography.BodySmall,
                        color = NovaChatColors.TextSecondary
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(avatarOptions) { avatarUrl ->
                            val isSelected = avatarUrl == uiState.user?.photoUrl
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(NovaChatColors.SurfaceVariant)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) NovaChatColors.Primary else NovaChatColors.Divider,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        showPhotoDialog = false
                                        viewModel.updateProfilePictureUrl(avatarUrl)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                SubcomposeAsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(avatarUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Avatar option",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                    loading = {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = NovaChatColors.Primary
                                        )
                                    }
                                )
                            }
                        }
                    }

                    if (!uiState.user?.photoUrl.isNullOrEmpty()) {
                        HorizontalDivider(color = NovaChatColors.Divider)
                        OutlinedButton(
                            onClick = {
                                showPhotoDialog = false
                                viewModel.removeProfilePicture()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Red)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Remove Photo", color = Color.Red)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPhotoDialog = false }) {
                    Text("Close", color = NovaChatColors.TextSecondary)
                }
            },
            containerColor = NovaChatColors.Surface
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        style = NovaChatTypography.HeadlineMedium,
                        color = NovaChatColors.TextPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NovaChatColors.Background,
                    titleContentColor = NovaChatColors.TextPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = NovaChatColors.Background
    ) { padding ->
        if (uiState.isLoading && uiState.user == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NovaChatColors.Primary)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            // ── Profile Header ──────────────────────────────────────────────────
            ProfileHeader(
                photoUrl = uiState.user?.photoUrl,
                displayName = uiState.user?.displayName ?: "User",
                email = uiState.user?.email ?: "Guest",
                isUpdating = uiState.isUpdating,
                onCameraClick = { showPhotoDialog = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Personal Info Section ───────────────────────────────────────────
            SettingsSectionTitle(title = "Personal Info")

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                color = NovaChatColors.Surface,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ProfileTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = "Name",
                        icon = Icons.Default.Person
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = NovaChatColors.Divider)
                    ProfileTextField(
                        value = editedBio,
                        onValueChange = { editedBio = it },
                        label = "About",
                        icon = Icons.Default.Info
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val hasChanges = editedName != (uiState.user?.displayName ?: "") ||
                    editedBio != (uiState.user?.bio ?: "")

            Button(
                onClick = {
                    if (editedName.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar("Name cannot be empty") }
                    } else {
                        viewModel.updateProfile(editedName, editedBio)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NovaChatColors.Primary,
                    disabledContainerColor = NovaChatColors.Primary.copy(alpha = 0.4f)
                ),
                enabled = !uiState.isUpdating && hasChanges
            ) {
                if (uiState.isUpdating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        text = if (hasChanges) "Save Changes" else "No Changes",
                        style = NovaChatTypography.TitleMedium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Preferences Section ─────────────────────────────────────────────
            SettingsSectionTitle(title = "Preferences")

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                color = NovaChatColors.Surface,
                shadowElevation = 2.dp
            ) {
                // Fixed: lambda reads the new value from the Switch callback (it: Boolean)
                SettingsSwitchItem(
                    title = "App Notifications",
                    subtitle = "Receive message notifications",
                    icon = Icons.Default.Notifications,
                    iconTint = NovaChatColors.Accent,
                    checked = uiState.user?.notificationsEnabled ?: true,
                    onCheckedChange = { newValue ->
                        viewModel.toggleSetting("notificationsEnabled", newValue)
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Privacy Section ─────────────────────────────────────────────────
            SettingsSectionTitle(title = "Privacy")

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                color = NovaChatColors.Surface,
                shadowElevation = 2.dp
            ) {
                Column {
                    SettingsSwitchItem(
                        title = "Read Receipts",
                        subtitle = "Show when you've read messages",
                        icon = Icons.Default.CheckCircle,
                        iconTint = NovaChatColors.Online,
                        checked = uiState.user?.readReceipts ?: true,
                        onCheckedChange = { newValue ->
                            viewModel.toggleSetting("readReceipts", newValue)
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = NovaChatColors.Divider)
                    SettingsSwitchItem(
                        title = "Last Seen",
                        subtitle = "Show when you were last online",
                        icon = Icons.Default.Visibility,
                        iconTint = NovaChatColors.Primary,
                        checked = uiState.user?.lastSeenVisible ?: true,
                        onCheckedChange = { newValue ->
                            viewModel.toggleSetting("lastSeenVisible", newValue)
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = NovaChatColors.Divider)
                    SettingsSwitchItem(
                        title = "About Visibility",
                        subtitle = "Allow others to see your bio",
                        icon = Icons.Default.Info,
                        iconTint = NovaChatColors.TextSecondary,
                        checked = uiState.user?.aboutVisible ?: true,
                        onCheckedChange = { newValue ->
                            viewModel.toggleSetting("aboutVisible", newValue)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Account Section ─────────────────────────────────────────────────
            SettingsSectionTitle(title = "Account")

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                color = NovaChatColors.Surface,
                shadowElevation = 2.dp,
                onClick = { showChangeEmailDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NovaChatColors.Primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = NovaChatColors.Primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Change Email",
                            style = NovaChatTypography.TitleMedium,
                            color = NovaChatColors.TextPrimary
                        )
                        Text(
                            text = uiState.user?.email ?: "No email set",
                            style = NovaChatTypography.BodySmall,
                            color = NovaChatColors.TextSecondary
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = NovaChatColors.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                color = NovaChatColors.Surface,
                shadowElevation = 2.dp,
                onClick = { showChangePasswordDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NovaChatColors.Primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = NovaChatColors.Primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Change Password",
                        style = NovaChatTypography.TitleMedium,
                        color = NovaChatColors.TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = NovaChatColors.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                color = NovaChatColors.Surface,
                shadowElevation = 2.dp,
                onClick = { showSignOutDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NovaChatColors.Accent.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = NovaChatColors.Accent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Sign Out",
                        style = NovaChatTypography.TitleMedium,
                        color = NovaChatColors.Accent
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = NovaChatColors.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                color = NovaChatColors.Surface,
                shadowElevation = 2.dp,
                onClick = { showDeleteAccountDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE53935).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = null,
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Delete Account",
                        style = NovaChatTypography.TitleMedium,
                        color = Color(0xFFE53935)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = NovaChatColors.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = NovaChatTypography.LabelMedium.copy(fontWeight = FontWeight.SemiBold),
        color = NovaChatColors.Primary,
        modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 10.dp)
    )
}

@Composable
private fun ProfileHeader(
    photoUrl: String?,
    displayName: String,
    email: String,
    isUpdating: Boolean,
    onCameraClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .padding(4.dp)
                    .clickable(onClick = onCameraClick),
                shape = CircleShape,
                color = NovaChatColors.Surface,
                shadowElevation = 8.dp
            ) {
                if (!photoUrl.isNullOrEmpty()) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(photoUrl)
                            .crossfade(true)
                            .memoryCachePolicy(CachePolicy.DISABLED)
                            .diskCachePolicy(CachePolicy.DISABLED)
                            .build(),
                        contentDescription = "Profile picture",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(listOf(NovaChatColors.Primary, NovaChatColors.PrimaryContainer))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier.fillMaxSize().background(
                                    Brush.linearGradient(listOf(NovaChatColors.Primary, NovaChatColors.PrimaryContainer))
                                ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                    color = Color.White,
                                    style = NovaChatTypography.HeadlineLarge.copy(fontSize = 48.sp)
                                )
                            }
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize().background(
                            Brush.linearGradient(listOf(NovaChatColors.Primary, NovaChatColors.PrimaryContainer))
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            color = Color.White,
                            style = NovaChatTypography.HeadlineLarge.copy(fontSize = 48.sp)
                        )
                    }
                }
            }

            // Camera edit button
            IconButton(
                onClick = onCameraClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-2).dp, y = (-2).dp)
                    .size(36.dp)
                    .background(NovaChatColors.Primary, CircleShape)
                    .border(2.dp, Color.White, CircleShape)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }

            if (isUpdating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(120.dp),
                    color = NovaChatColors.Primary,
                    strokeWidth = 3.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = displayName, style = NovaChatTypography.HeadlineMedium, color = NovaChatColors.TextPrimary)
        Text(text = email, style = NovaChatTypography.BodyMedium, color = NovaChatColors.TextSecondary)
    }
}

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(NovaChatColors.Primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = NovaChatColors.Primary, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = NovaChatTypography.LabelSmall, color = NovaChatColors.TextSecondary)
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = NovaChatTypography.BodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = NovaChatColors.TextPrimary
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = NovaChatColors.Primary
                ),
                singleLine = true
            )
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    subtitle: String = "",
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color = NovaChatColors.Primary,
    checked: Boolean,
    // Fixed: lambda param 'newValue' comes from the Switch component
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = title, style = NovaChatTypography.TitleMedium, color = NovaChatColors.TextPrimary)
                if (subtitle.isNotEmpty()) {
                    Text(text = subtitle, style = NovaChatTypography.BodySmall, color = NovaChatColors.TextSecondary)
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange, // directly forwards the new Boolean value
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = NovaChatColors.Primary,
                uncheckedThumbColor = NovaChatColors.TextSecondary,
                uncheckedTrackColor = NovaChatColors.Divider
            )
        )
    }
}
