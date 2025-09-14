package com.example.chords2.ui.theme.imagevector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Check_indeterminate_small: ImageVector
    get() {
        if (_Check_indeterminate_small != null) return _Check_indeterminate_small!!

        _Check_indeterminate_small = ImageVector.Builder(
            name = "Check_indeterminate_small",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(240f, 520f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(480f)
                verticalLineToRelative(80f)
                close()
            }
        }.build()

        return _Check_indeterminate_small!!
    }

private var _Check_indeterminate_small: ImageVector? = null

