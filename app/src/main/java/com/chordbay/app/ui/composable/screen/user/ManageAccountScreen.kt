package com.chordbay.app.ui.composable.screen.user


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chordbay.app.data.helper.isPasswordValid
import com.chordbay.app.ui.composable.component.topappbar.MyTopAppBar
import com.chordbay.app.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun ManageAccountScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val canNavigateBack = navController.previousBackStackEntry != null
    val emailState = authViewModel.userEmail.collectAsState()
    val emailText = emailState.value ?: "Not logged in"
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val showDeleteAccountDialog = rememberSaveable { mutableStateOf(false) }
    val showChangePassDialog = rememberSaveable { mutableStateOf(false) }
    val changePasswordSuccess = authViewModel.changePasswordSuccess.collectAsState()
    val error = authViewModel.error.collectAsState()
    val oldPassText = rememberSaveable { mutableStateOf("") }
    val newPassText = rememberSaveable { mutableStateOf("") }
    val confirmPassText = rememberSaveable { mutableStateOf("") }

    LaunchedEffect(changePasswordSuccess.value) {
        if (changePasswordSuccess.value) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    "Password changed successfully."
                )
            }
            authViewModel.onChangePasswordSuccess()
        }
    }
    LaunchedEffect(error.value) {
        if (error.value != null) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    error.value ?: "An unknown error occurred."
                )
            }
            authViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Manage Account",
                navigationIcon = if (canNavigateBack) Icons.AutoMirrored.Filled.ArrowBack else null,
                navigationIconContentDescription = if (canNavigateBack) "Back" else null,
                onNavigationIconClick = if (canNavigateBack) {
                    { navController.navigateUp() }
                } else null,
                actions = {}
            )
        },
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.Top)
            ) {
                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Your account",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Manage your account.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Account details",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )

                        Text(
                            text = "Email",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = emailText,
                            style = MaterialTheme.typography.bodyMedium,
                        )


                        Button(
                            onClick = {
                                showChangePassDialog.value = true
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            enabled = emailState.value != null
                        ) {
                            Text(text = "Change password")
                        }

                        HorizontalDivider(Modifier.padding(0.dp))

                        Text(
                            text = "Options",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(
                                onClick = {
                                    authViewModel.logoutUser()
                                },
                                enabled = emailState.value != null
                            ) {
                                Text(text = "Sign out")
                            }

                            TextButton(
                                onClick = {
                                    showDeleteAccountDialog.value = true
                                },
                                enabled = emailState.value != null
                            ) {
                                Text("Delete account", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            DeleteAccountDialog(
                showDialog = showDeleteAccountDialog.value,
                onDismiss = { showDeleteAccountDialog.value = false },
                onConfirm = {
                    showDeleteAccountDialog.value = false
                    authViewModel.deleteAccount()
                    navController.popBackStack()
                }
            )
            val areAllValid = oldPassText.value.isPasswordValid() &&
                    newPassText.value.isPasswordValid() &&
                    confirmPassText.value.isPasswordValid()
            ChangePasswordDialog(
                showDialog = showChangePassDialog.value,
                onDismiss = { showChangePassDialog.value = false },
                oldPassText = oldPassText.value,
                newPassText = newPassText.value,
                confirmPassText = confirmPassText.value,
                onConfirm = {
                    if (!areAllValid) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Please ensure all password fields are valid."
                            )
                        }
                        return@ChangePasswordDialog
                    }
                    showChangePassDialog.value = false
                    authViewModel.changePassword(
                        email = emailState.value ?: return@ChangePasswordDialog,
                        oldPassword = oldPassText.value,
                        newPassword = newPassText.value,
                    )
                    oldPassText.value = ""
                    newPassText.value = ""
                    confirmPassText.value = ""
                },
                onOldPassChange = { oldPassText.value = it },
                onNewPassChange = { newPassText.value = it },
                onConfirmPassChange = { confirmPassText.value = it },
                onNotMatchingPass = {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "New passwords do not match."
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun DeleteAccountDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Delete Account") },
            text = {
                Text(
                    """
                Are you sure you want to delete your account?
                This action cannot be undone.
                All your uploaded songs will be permanently deleted.
                """.trimIndent()
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ChangePasswordDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    oldPassText: String,
    newPassText: String,
    confirmPassText: String,
    onConfirm: () -> Unit,
    onNotMatchingPass: () -> Unit,
    onOldPassChange: (String) -> Unit,
    onNewPassChange: (String) -> Unit,
    onConfirmPassChange: (String) -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Change Password") },
            text = {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PasswordTextField(
                            password = oldPassText,
                            onPasswordChange = onOldPassChange,
                            label = "Current Password"
                        )
                        PasswordTextField(
                            password = newPassText,
                            onPasswordChange = onNewPassChange,
                            label = "New Password",
                            showSupportingText = true
                        )
                        PasswordTextField(
                            password = confirmPassText,
                            onPasswordChange = onConfirmPassChange,
                            label = "Confirm Password"
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newPassText == confirmPassText) {
                            onConfirm()
                        } else {
                            onNotMatchingPass()
                        }
                    }
                ) {
                    Text("Change Password")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun PasswordTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    showSupportingText: Boolean = false
) {
    var showPassword by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = password,
        onValueChange = {
            onPasswordChange(it)
        },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall
            )
        },
        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = { showPassword = !showPassword }) {
                Icon(
                    if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = if (showPassword) "Hide password" else "Show password"
                )
            }
        },
        singleLine = true,
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            keyboardType = KeyboardType.Password
        ),
        isError = password.isNotBlank() && !password.isPasswordValid(),
        supportingText = {
            if (showSupportingText) {
                AnimatedVisibility(visible = !password.isPasswordValid()) {
                    Text(
                        """
                            Password must contain:
                                • At least 9 characters
                                • Uppercase and lowercase letters
                                • At least one digit
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
    )
}