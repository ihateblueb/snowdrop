package site.remlit.snowdrop.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
expect fun getLightColorScheme(): ColorScheme
@Composable
expect fun getDarkColorScheme(): ColorScheme

@Composable
fun AppTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	content: @Composable () -> Unit
) {
	MaterialTheme(
		colorScheme = if (darkTheme) getDarkColorScheme() else getLightColorScheme(),
		content = content
	)
}
