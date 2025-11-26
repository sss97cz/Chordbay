package com.example.chords2.ui.composable.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chords2.ui.composable.component.topappbar.MyTopAppBar
import com.example.chords2.ui.theme.imagevector.Check_indeterminate_small

@Composable
fun HelpScreen(
    navController: NavController
) {
    val canNavigateBack = navController.previousBackStackEntry != null

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Help",
                onNavigationIconClick = { navController.popBackStack() },
                navigationIcon = Icons.AutoMirrored.Default.ArrowBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .padding(top = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {

            ExpandableInfoItem(
                title = "How to Create a new song?",
                content = "You can create a new song by clicking the '+' button in the library tab located in the top bar. Then you can start writing your song in the song editor."
            )
            ExpandableInfoItem(
                title = "How to add Chords to a song?",
                content = """
                     To add chords to a song and make them transposable, open the song editor and type the chord name enclosed in square brackets.
                     
                     Chords written like this will be detected and can be transposed:
                     [C]Loren [Am]ipsum...
                     Chords written without brackets won’t be detected:
                     C Loren Am ipsum...
                """.trimIndent()
            )
            ExpandableInfoItem(
                title = "How to share songs?",
                content = """
                    To share your songs with other users of Chordbay, first you need to create an account and log in.
                    Once logged in, select the songs you want to share and click the "Share" icon in the menu.
                    Your songs will be uploaded to our servers and made available to other users. 
                    You can also upload the songs as private, so only you can access them from any device when logged in.
                    When sharing songs, please make sure the chords are properly formatted with brackets to ensure a better experience for you and other users.
                    Also, avoid sharing copyrighted songs, otherwise your account will be suspended. 
                """.trimIndent()
            )
            ExpandableInfoItem(
                title = "Can I share copyrighted songs?",
                content = "No, sharing copyrighted songs is not allowed and is strictly against TOS! Please make sure to only share songs that you have the rights to share. Violating this rule will result in your account being suspended."
            )
            ExpandableInfoItem(
                title = "How to create an account?",
                content = """
                    To create an account, open the side menu and click on "Sign In".
                    Then, click on "Create Account" and fill in the required information such as email and password.
                    After creating your account, you will receive a verification email. After verifying, you may log in and start sharing songs!
                """.trimIndent()
            )
            ExpandableInfoItem(
                title = "How to reset my password?",
                content = "To reset your password, go to the sign-in screen, enter your email address and click on 'Forgot Password'. You will receive an email to reset your password."
            )
            ExpandableInfoItem(
                title = "How to download shared songs?",
                content = "To download shared songs, go to the 'Browse' tab. You can search songs by artist or title. Once you find a song you like, click on it and then tap the 'Download' button to save it to your library."
            )
            ExpandableInfoItem(
                title = "How to create a playlist?",
                content = "You can create a playlist clicking add playlist in library tab menu. Then, give your playlist a name and start adding songs to it!"
            )
            ExpandableInfoItem(
                title = "How to add songs to a playlist?",
                content = "To add songs to a playlist, select songs in your library by long-pressing on them, then tap the 'Add to Playlist' option and choose the desired playlist."
            )
            ExpandableInfoItem(
                title = "How to transpose chords?",
                content = "To transpose chords, open a song and use the transpose buttons located at the top of the screen."
            )
            ExpandableInfoItem(
                title = "What is Chord Format?",
                content = """
                Chord Format refers to the system used in different regions to name musical notes. Chordbay supports two formats:

                English (Bb/B)

                German (B/H) — in this format, the English B is written as H.

                Each song stores its own chord format.
                When creating a new song, you can choose which format to use by tapping the settings icon in the top bar and selecting your preferred format.

                You can also change the app’s default chord format in the Settings menu.
                This affects:
                The format used for newly created songs, and how chords are displayed in the app.
                It does not change the underlying chord data of your existing songs.
                """.trimIndent()
            )
        }
    }
}

@Composable
private fun ExpandableInfoItem(
    title: String,
    content: String
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { expanded = !expanded }
                ) {
                    Icon(
                        imageVector = if (!expanded) Icons.Default.Add else Check_indeterminate_small,
                        contentDescription = null
                    )
                }
            }
            if (expanded) {
                HorizontalDivider()
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun ExpandableInfoItemPreview() {
    ExpandableInfoItem(
        title = "How to create a playlist? ",
        content = "To create a playlist, go to the Playlists section and tap on the 'Create Playlist' button. Enter a name for your playlist and start adding songs!"
    )
}
