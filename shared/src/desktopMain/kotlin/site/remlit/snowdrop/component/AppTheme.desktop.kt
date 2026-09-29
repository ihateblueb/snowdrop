package site.remlit.snowdrop.component

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun getLightColorScheme(): ColorScheme = lightColorScheme()

@Composable
actual fun getDarkColorScheme(): ColorScheme = darkColorScheme()
