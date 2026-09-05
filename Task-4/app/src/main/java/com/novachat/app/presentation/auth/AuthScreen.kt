package com.novachat.app.presentation.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.novachat.app.R
import com.novachat.app.presentation.ui.theme.NovaChatColors
import com.novachat.app.presentation.ui.theme.NovaChatTypography
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onAuthSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.resetState()
    }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) onAuthSuccess()
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            scope.launch {
                snackbarHostState.showSnackbar(msg)
                viewModel.clearError()
            }
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { msg ->
            scope.launch {
                snackbarHostState.showSnackbar(msg)
                viewModel.clearSuccess()
            }
        }
    }

    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    if (showForgotPasswordDialog) {
        var resetEmail by remember { mutableStateOf(uiState.email) }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = {
                Text(
                    text = "Reset Password",
                    style = NovaChatTypography.TitleMedium,
                    color = NovaChatColors.TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Enter your registered email address and we'll send you a password reset link.",
                        style = NovaChatTypography.BodyMedium,
                        color = NovaChatColors.TextSecondary
                    )
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Email Address") },
                        placeholder = { Text("ayush.sharma@example.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NovaChatColors.Primary,
                            unfocusedBorderColor = NovaChatColors.Divider,
                            focusedTextColor = NovaChatColors.TextPrimary,
                            unfocusedTextColor = NovaChatColors.TextPrimary,
                            cursorColor = NovaChatColors.Primary
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showForgotPasswordDialog = false
                        viewModel.sendPasswordReset(resetEmail)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NovaChatColors.Primary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = resetEmail.isNotBlank()
                ) {
                    Text("Send Reset Link", color = NovaChatColors.TextOnPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Cancel", color = NovaChatColors.TextSecondary)
                }
            },
            containerColor = NovaChatColors.Surface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NovaChatColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // ── Brand Header ───────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(NovaChatColors.Primary, NovaChatColors.PrimaryContainer)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "NovaChat Logo",
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "NovaChat",
                color = NovaChatColors.Primary,
                style = NovaChatTypography.HeadlineLarge
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (uiState.isSignUpMode) "Create your account" else "Welcome back",
                color = NovaChatColors.TextSecondary,
                style = NovaChatTypography.BodyLarge
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ── Auth Card ──────────────────────────────────────────────────────
            AuthCard(
                uiState = uiState,
                onEmailChanged = viewModel::onEmailChanged,
                onPasswordChanged = viewModel::onPasswordChanged,
                onDisplayNameChanged = viewModel::onDisplayNameChanged,
                onSignIn = viewModel::signIn,
                onSignUp = viewModel::signUp,
                onToggleMode = viewModel::toggleSignUpMode,
                onForgotPassword = { showForgotPasswordDialog = true }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = NovaChatColors.TextPrimary,
                contentColor = NovaChatColors.Surface
            )
        }
    }
}

@Composable
private fun AuthCard(
    uiState: AuthUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onDisplayNameChanged: (String) -> Unit,
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
    onToggleMode: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(NovaChatColors.Surface)
            .padding(horizontal = 24.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Display Name (Sign-up only) ────────────────────────────────────────
        AnimatedVisibility(
            visible = uiState.isSignUpMode,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            OutlinedTextField(
                value = uiState.displayName,
                onValueChange = onDisplayNameChanged,
                label = { Text("Display Name") },
                placeholder = { Text("e.g. Ayush Sharma") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = NovaChatColors.TextSecondary
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NovaChatColors.Primary,
                    unfocusedBorderColor = NovaChatColors.Divider,
                    focusedLabelColor = NovaChatColors.Primary,
                    cursorColor = NovaChatColors.Primary,
                    focusedTextColor = NovaChatColors.TextPrimary,
                    unfocusedTextColor = NovaChatColors.TextPrimary
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
        }

        // ── Email Field ────────────────────────────────────────────────────────
        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChanged,
            label = { Text("Email Address") },
            placeholder = { Text("ayush.sharma@example.com") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = NovaChatColors.TextSecondary
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NovaChatColors.Primary,
                unfocusedBorderColor = NovaChatColors.Divider,
                focusedLabelColor = NovaChatColors.Primary,
                cursorColor = NovaChatColors.Primary,
                focusedTextColor = NovaChatColors.TextPrimary,
                unfocusedTextColor = NovaChatColors.TextPrimary
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )

        // ── Password Field ─────────────────────────────────────────────────────
        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChanged,
            label = { Text("Password") },
            placeholder = { Text("At least 6 characters") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = NovaChatColors.TextSecondary
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NovaChatColors.Primary,
                unfocusedBorderColor = NovaChatColors.Divider,
                focusedLabelColor = NovaChatColors.Primary,
                cursorColor = NovaChatColors.Primary,
                focusedTextColor = NovaChatColors.TextPrimary,
                unfocusedTextColor = NovaChatColors.TextPrimary
            ),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = NovaChatColors.TextSecondary
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (uiState.isSignUpMode) onSignUp() else onSignIn()
                }
            )
        )

        // ── Forgot Password Button (Sign-in only) ──────────────────────────────
        if (!uiState.isSignUpMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onForgotPassword,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "Forgot Password?",
                        color = NovaChatColors.Primary,
                        style = NovaChatTypography.BodySmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(4.dp))
        }

        // ── Primary Action Button ──────────────────────────────────────────────
        Button(
            onClick = { if (uiState.isSignUpMode) onSignUp() else onSignIn() },
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NovaChatColors.Primary,
                disabledContainerColor = NovaChatColors.Primary.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = NovaChatColors.TextOnPrimary,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = if (uiState.isSignUpMode) "Create Account" else "Sign In",
                    color = NovaChatColors.TextOnPrimary,
                    style = NovaChatTypography.TitleMedium
                )
            }
        }

        // ── Toggle Mode ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (uiState.isSignUpMode) "Already have an account?" else "Don't have an account?",
                color = NovaChatColors.TextSecondary,
                style = NovaChatTypography.BodyMedium
            )
            TextButton(onClick = onToggleMode) {
                Text(
                    text = if (uiState.isSignUpMode) "Sign In" else "Sign Up",
                    color = NovaChatColors.Primary,
                    style = NovaChatTypography.TitleMedium
                )
            }
        }
    }
}

@Composable
private fun NcTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = NovaChatColors.TextSecondary) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        textStyle = NovaChatTypography.BodyLarge,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NovaChatColors.Primary.copy(alpha = 0.6f),
            unfocusedBorderColor = NovaChatColors.Divider,
            cursorColor = NovaChatColors.Primary,
            focusedContainerColor = NovaChatColors.InputBackground,
            unfocusedContainerColor = NovaChatColors.InputBackground,
            focusedTextColor = NovaChatColors.TextPrimary,
            unfocusedTextColor = NovaChatColors.TextPrimary,
            focusedLabelColor = NovaChatColors.Primary,
            unfocusedLabelColor = NovaChatColors.TextSecondary
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    )
}
