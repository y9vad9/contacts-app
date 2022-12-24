package com.y9vad9.contacts.ui.resources

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun ContactsTheme(block: @Composable () -> Unit) {
    val isDarkTheme = isSystemInDarkTheme()
    val colors = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    val systemUiController = rememberSystemUiController()

    DisposableEffect(systemUiController, isDarkTheme) {
        systemUiController.setSystemBarsColor(
            color = colors.background,
            darkIcons = !isDarkTheme
        )

        onDispose {}
    }

    MaterialTheme(colors) {
        Surface {
            block()
        }
    }
}