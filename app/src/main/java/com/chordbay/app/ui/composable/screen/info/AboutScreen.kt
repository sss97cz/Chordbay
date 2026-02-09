package com.chordbay.app.ui.composable.screen.info

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.chordbay.app.R
import com.chordbay.app.ui.composable.component.topappbar.MyTopAppBar

private const val KOFI_URL = "https://ko-fi.com/chordbaysongbook"
private const val GITHUB_URL = "https://github.com/sss97cz/Chordbay"

@Composable
fun AboutScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val canNavigateBack = navController.previousBackStackEntry != null

    val packageInfo = remember(context) {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    val versionName = packageInfo?.versionName ?: "Unknown"

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "About app & Support",
                onNavigationIconClick = { if (canNavigateBack) navController.navigateUp() },
                navigationIcon = if (canNavigateBack) Icons.AutoMirrored.Default.ArrowBack else null
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text (
                text = "Chordbay - Songbook",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )


            // Info Footer
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Created by Šimon Kubant",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Version $versionName",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            HorizontalDivider()
            // Support Section
            Text(
                text = "Support Development",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Chordbay is an open source project. If you find the app useful, consider supporting its development.",
                style = MaterialTheme.typography.bodyMedium
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(role = Role.Button) {
                        val intent = Intent(Intent.ACTION_VIEW, KOFI_URL.toUri())
                        context.startActivity(intent)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.kofi_symbol),
                        contentDescription = "Ko-fi Logo",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(32.dp)
                    )
                    Text(
                        text = "Support on Ko-fi",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(role = Role.Button) {
                        val intent = Intent(Intent.ACTION_VIEW, GITHUB_URL.toUri())
                        context.startActivity(intent)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.github_mark),
                        contentDescription = "GitHub Logo",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(32.dp)
                    )
                    Text(
                        text = "Contribute on GitHub",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

        }
    }
}


