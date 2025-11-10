package com.example.chords2.ui.composable.screen.user

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chords2.ui.composable.component.topappbar.MyTopAppBar
import com.example.chords2.ui.composable.navigation.Paths
import com.example.chords2.ui.viewmodel.AuthViewModel
import com.example.chords2.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    val isLoading = authViewModel.loading.collectAsState()
    var errorText by remember { mutableStateOf<String?>(null) }

    val isEmailValid = email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPasswordValid = password.length >= 9 &&
            password.any { it.isUpperCase() } &&
            password.any { it.isDigit() } &&
            password.any { it.isLowerCase() }
    val canSubmit = isEmailValid && isPasswordValid && !isLoading.value
    val canNavigateBack = navController.previousBackStackEntry != null

    val isLoggedIn = authViewModel.isUserLoggedIn.collectAsState()
    val errorMessage = authViewModel.error.collectAsState()

    LaunchedEffect(isLoading.value) {
        if (!isLoading.value) {
            if (isLoggedIn.value) {

                navController.navigate(Paths.HomePath.route) {
                    popUpTo(Paths.LoginPath.route) {
                        inclusive = true
                    }
                }
                mainViewModel.fetchMyRemoteSongs()
            } else if (errorMessage.value != null) {
                scope.launch {
                    snackbarHostState.showSnackbar(errorMessage.value!!)
                    authViewModel.clearError()
                }
            }
        }
    }
    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Sign In",
                navigationIcon = if (canNavigateBack) Icons.AutoMirrored.Filled.ArrowBack else null,
                navigationIconContentDescription = if (canNavigateBack) "Back" else null,
                onNavigationIconClick = if (canNavigateBack) {
                    {
                        navController.navigateUp()
                    }
                } else null,
                actions = {

                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
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
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top)
            ) {
                Spacer(Modifier.height(12.dp))

                // Header
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "🔐",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                Text(
                    text = "Welcome back",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "Sign in to access all app features.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                // Card with inputs
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                errorText = null
                            },
                            label = { Text("Email") },
                            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                            singleLine = true,
                            isError = email.isNotBlank() && !isEmailValid,
                            supportingText = {
                                AnimatedVisibility(visible = email.isNotBlank() && !isEmailValid) {
                                    Text("Please enter a valid email.")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                errorText = null
                            },
                            label = { Text("Password") },
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
                            isError = password.isNotBlank() && !isPasswordValid,
                            supportingText = {
                                AnimatedVisibility(visible = !isPasswordValid) {
                                    Text("""
                                        Password must contain:
                                        • At least 9 characters
                                        • Uppercase and lowercase letters
                                        • At least one digit
                                    """.trimIndent()
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),

                            )

                        AnimatedVisibility(visible = errorText != null) {
                            Text(
                                text = errorText.orEmpty(),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                if (!isEmailValid || !isPasswordValid) {
                                    errorText = "Please fix the highlighted fields."
                                    return@Button
                                }
                                authViewModel.loginUser(email, password)
                            },
                            enabled = canSubmit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            if (isLoading.value) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(Modifier.width(12.dp))
                            }
                            Text("Continue")
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { /* TODO: navigate to reset password */ }) {
                                Text("Forgot password?")
                            }
                            TextButton(onClick = {
                                navController.navigate(Paths.RegisterPath.route)
                            }) {
                                Text("Create account")
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            HorizontalDivider(Modifier.weight(1f))
                            Text(
                                "  or  ",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            HorizontalDivider(Modifier.weight(1f))
                        }
                        TextButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Continue as guest")
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
            }

            if (isLoading.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(enabled = false) {}
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f))
                )
            }
        }
    }
}

