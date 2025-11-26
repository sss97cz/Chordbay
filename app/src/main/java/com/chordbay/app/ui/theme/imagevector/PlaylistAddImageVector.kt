package com.chordbay.app.ui.theme.imagevector

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val Playlist_add: ImageVector
    get() =
        ImageVector.Builder(
            name = "Playlist_add",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            // First bar
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(120f, 640f)
                horizontalLineToRelative(280f)
                verticalLineToRelative(80f)
                horizontalLineToRelative(-280f)
                verticalLineToRelative(-80f)
                close()
            }
            // Second bar
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(120f, 480f)
                horizontalLineToRelative(440f)
                verticalLineToRelative(80f)
                horizontalLineToRelative(-440f)
                verticalLineToRelative(-80f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(120f, 320f)
                horizontalLineToRelative(440f)
                verticalLineToRelative(80f)
                horizontalLineToRelative(-440f)
                verticalLineToRelative(-80f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(720f, 320f)
                verticalLineToRelative(160f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(80f)
                horizontalLineToRelative(-160f)
                verticalLineToRelative(160f)
                horizontalLineToRelative(-80f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(-160f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(80f)
                close()
            }
        }.build()

@Preview
@Composable
fun PlaylistAddPreview() {
    androidx.compose.material3.Icon(
        imageVector = Playlist_add,
        contentDescription = "Playlist add",
        tint = Color.Black
    )
}

