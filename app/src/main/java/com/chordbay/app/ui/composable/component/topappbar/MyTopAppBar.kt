package com.chordbay.app.ui.composable.component.topappbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    title: String,
    subtitle: String? = null,
    onNavigationIconClick: (() -> Unit)? = null,
    navigationIcon: ImageVector? = null,
    navigationIconContentDescription: String? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Column() {
                var isOverflowing by remember { mutableStateOf(false) }
                val context = LocalContext.current

                if (isOverflowing) {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                            TooltipAnchorPosition.Below,
                        ),
                        state = rememberTooltipState(),
                        tooltip = {
                            Box(
                                Modifier
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                                    .padding(4.dp)
                            ) {
                                Text(text = title)
                            }
                        },
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            onTextLayout = { result ->
                                val overflow = result.hasVisualOverflow
                                if (isOverflowing != overflow) isOverflowing = overflow
                            }
                        )
                    }
                } else {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = { result ->
                            val overflow = result.hasVisualOverflow
                            if (isOverflowing != overflow) isOverflowing = overflow
                        }
                    )
                }
                if (subtitle != null) {
                    var isSubtitleOverflowing by remember { mutableStateOf(false) }
                    if (isSubtitleOverflowing) {
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Below,
                            ),
                            state = rememberTooltipState(),
                            tooltip = {
                                Box(
                                    Modifier
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                                        .padding(4.dp)
                                ) {
                                    Text(text = subtitle)
                                }
                            },
                        ) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                onTextLayout = { result ->
                                    val overflow = result.hasVisualOverflow
                                    if (isSubtitleOverflowing != overflow) isSubtitleOverflowing =
                                        overflow
                                }
                            )
                        }
                    } else {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            onTextLayout = { result ->
                                val overflow = result.hasVisualOverflow
                                if (isSubtitleOverflowing != overflow) isSubtitleOverflowing =
                                    overflow
                            }
                        )
                    }
                }
            }
        },
        navigationIcon = {
            if (navigationIcon == null || onNavigationIconClick == null) {
                return@TopAppBar
            }
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = navigationIcon,
                    contentDescription = navigationIconContentDescription
                )
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.primary
        ),
    )
}