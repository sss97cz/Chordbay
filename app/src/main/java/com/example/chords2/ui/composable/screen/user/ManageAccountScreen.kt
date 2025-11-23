package com.example.chords2.ui.composable.screen.user


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chords2.ui.composable.component.topappbar.MyTopAppBar
import com.example.chords2.ui.composable.navigation.Paths
import com.example.chords2.ui.viewmodel.AuthViewModel
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
                    text = "Manage your sign-in details and security.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                // Main card
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
                            style = MaterialTheme.typography.bodyMedium
                        )


                        Button(
                            onClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "not implemented yet"
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            enabled = emailState.value != null
                        ) {
                            Text(text = "Send password reset email")
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
                            Button(
                                onClick = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "not implemented yet"
                                        )
                                    }
                                },
                                enabled = emailState.value != null
                            ) {
                                Text(text = "Sign out")
                            }

                            TextButton(
                                onClick = {
//                                    authViewModel.deleteAccount()
//                                    navController.popBackStack()
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
            text = { Text(
                """
                Are you sure you want to delete your account?
                This action cannot be undone.
                All your uploaded songs will be permanently deleted.
                """.trimIndent()
            ) },
            confirmButton = {
                TextButton(
                    onClick = onConfirm,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
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