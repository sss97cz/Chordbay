package com.chordbay.app.ui.composable.screen.user

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chordbay.app.ui.composable.component.topappbar.MyTopAppBar
import com.chordbay.app.ui.composable.navigation.Paths
import com.chordbay.app.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyEmailScreen(
    authViewModel: AuthViewModel,
    navController: NavController,
    email: String
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading = authViewModel.loading.collectAsState()
    val errorMessage = authViewModel.error.collectAsState()
    val canNavigateBack = navController.previousBackStackEntry != null
    val context = LocalContext.current

    if (email.isEmpty()){
        navController.popBackStack()
    }

    LaunchedEffect(isLoading.value) {
        if (!isLoading.value && errorMessage.value != null) {
            scope.launch { snackbarHostState.showSnackbar(errorMessage.value!!) }
            authViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Verify email",
                navigationIconContentDescription = if (canNavigateBack) "Back" else null,
                onNavigationIconClick = if (canNavigateBack) { { navController.navigateUp() } } else null,
                actions = {}
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
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
                verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.Top)
            ) {
                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Verify your email",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "We sent a verification link to your email: $email. Open it and tap verify to continue.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

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
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                navController.navigate(Paths.LoginPath.route) {
                                    popUpTo(Paths.VerifyEmailPath.route) { inclusive = true }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("I verified, continue")
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            HorizontalDivider(Modifier.weight(1f))
                            Text("  or  ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            HorizontalDivider(Modifier.weight(1f))
                        }

                        TextButton(
                            onClick = {
                                openEmailClient(context, snackbarHostState, scope)
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Open email app")
                        }

                        TextButton(
                            onClick = {
                                authViewModel.resendVerificationEmail(email)
                                scope.launch { snackbarHostState.showSnackbar("Verification email re-sent.") }
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Resend email")
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

            AnimatedVisibility(visible = isLoading.value) {
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

private fun openEmailClient(context: Context, snackbarHostState: SnackbarHostState, scope: CoroutineScope) {
    val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_EMAIL)
        .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        scope.launch { snackbarHostState.showSnackbar("No email client found") }
    }
}
