package com.chordbay.app.ui.composable.screen.info

import android.content.Intent
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.chordbay.app.ui.composable.component.topappbar.MyTopAppBar

private val KOFI_URL = "https://ko-fi.com/chordbaysongbook"
private val GITHUB_URL = "https://github.com/sss97cz/Chordbay"
@Composable
fun AboutScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val canNavigateBack = navController.previousBackStackEntry != null
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
                .padding(8.dp)
        ) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = "Chordbay is an open source project. If you find the app useful, you can support the app on Ko-fi",
                style = MaterialTheme.typography.titleSmall
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
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
                    Text(
                        text = "Support the app",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
            Text(
                modifier = Modifier.padding(top = 12.dp).padding(4.dp),
                text = "Or you can support by contributing or reporting bugs on GitHub",
                style = MaterialTheme.typography.titleSmall
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
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
                    Text(
                        text = "GitHub",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}
