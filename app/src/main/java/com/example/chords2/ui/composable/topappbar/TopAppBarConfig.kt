package com.example.chords2.ui.composable.topappbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

data class TopAppBarConfig(
    var title: String = "",
    val actions: @Composable RowScope.() -> Unit = {}
)
