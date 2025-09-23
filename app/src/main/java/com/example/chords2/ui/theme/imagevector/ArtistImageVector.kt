package com.example.chords2.ui.theme.imagevector

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// No caching to avoid stale vector during hot reload
val Artist: ImageVector
    get() = ImageVector.Builder(
        name = "Artist",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        // Brush / accessory
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(740f, 400f)
            horizontalLineToRelative(140f)
            verticalLineToRelative(80f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(220f)
            quadToRelative(0f, 42f, -29f, 71f)
            reflectiveQuadToRelative(-71f, 29f)
            reflectiveQuadToRelative(-71f, -29f)
            reflectiveQuadToRelative(-29f, -71f)
            reflectiveQuadToRelative(29f, -71f)
            reflectiveQuadToRelative(71f, -29f)
            quadToRelative(8f, 0f, 18f, 1.5f)
            reflectiveQuadToRelative(22f, 6.5f)
            close()
        }
        // Body
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(120f, 800f)
            verticalLineToRelative(-112f)
            quadToRelative(0f, -35f, 17.5f, -63f)
            reflectiveQuadToRelative(46.5f, -43f)
            quadToRelative(62f, -31f, 126f, -46.5f)
            reflectiveQuadTo(440f, 520f)
            quadToRelative(42f, 0f, 83.5f, 6.5f)
            reflectiveQuadTo(607f, 546f)
            quadToRelative(-20f, 12f, -36f, 29f)
            reflectiveQuadToRelative(-28f, 37f)
            quadToRelative(-26f, -6f, -51.5f, -9f)
            reflectiveQuadToRelative(-51.5f, -3f)
            quadToRelative(-57f, 0f, -112f, 14f)
            reflectiveQuadToRelative(-108f, 40f)
            quadToRelative(-9f, 5f, -14.5f, 14f)
            reflectiveQuadToRelative(-5.5f, 20f)
            verticalLineToRelative(32f)
            horizontalLineToRelative(321f)
            quadToRelative(2f, 20f, 9.5f, 40f)
            reflectiveQuadToRelative(20.5f, 40f)
            close()
        }
        // Head (circle)
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(440f, 480f)
            quadToRelative(-66f, 0f, -113f, -47f)
            reflectiveQuadToRelative(-47f, -113f)
            reflectiveQuadToRelative(47f, -113f)
            reflectiveQuadToRelative(113f, -47f)
            reflectiveQuadToRelative(113f, 47f)
            reflectiveQuadToRelative(47f, 113f)
            reflectiveQuadToRelative(-47f, 113f)
            reflectiveQuadToRelative(-113f, 47f)
            moveToRelative(0f, -80f)
            quadToRelative(33f, 0f, 56.5f, -23.5f)
            reflectiveQuadTo(520f, 320f)
            reflectiveQuadToRelative(-23.5f, -56.5f)
            reflectiveQuadTo(440f, 240f)
            reflectiveQuadToRelative(-56.5f, 23.5f)
            reflectiveQuadTo(360f, 320f)
            reflectiveQuadToRelative(23.5f, 56.5f)
            reflectiveQuadTo(440f, 400f)
        }
    }.build()


