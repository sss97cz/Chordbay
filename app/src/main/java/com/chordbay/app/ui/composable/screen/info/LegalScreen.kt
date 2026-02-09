package com.chordbay.app.ui.composable.screen.info

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.chordbay.app.ui.composable.component.topappbar.MyTopAppBar

private const val TERMS_URL = "https://chordbay.eu/terms"
private const val PRIVACY_URL = "https://chordbay.eu/privacy"

@Composable
fun LegalScreen(
    navController: NavController
) {
    val canNavigateBack = navController.previousBackStackEntry != null
    val context = LocalContext.current

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Legal",
                onNavigationIconClick = { if (canNavigateBack) navController.navigateUp() },
                navigationIcon = if (canNavigateBack) Icons.AutoMirrored.Default.ArrowBack else null
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(role = Role.Button) {
                        val intent = Intent(Intent.ACTION_VIEW, TERMS_URL.toUri())
                        context.startActivity(intent)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Terms of Service",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(role = Role.Button) {
                        val intent = Intent(Intent.ACTION_VIEW, PRIVACY_URL.toUri())
                        context.startActivity(intent)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Privacy Policy",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
