package site.remlit.snowdrop.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
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
		typography = Typography(
			headlineLarge = MaterialTheme.typography.headlineLarge.copy(),
			headlineMedium = MaterialTheme.typography.headlineMedium.copy(),
			headlineSmall = MaterialTheme.typography.headlineSmall.copy(),

			titleLarge = MaterialTheme.typography.titleLarge.copy(),
			titleMedium = MaterialTheme.typography.titleMedium.copy(),
			titleSmall = MaterialTheme.typography.titleSmall.copy(),

			displayLarge = MaterialTheme.typography.displayLarge.copy(),
			displayMedium = MaterialTheme.typography.displayMedium.copy(),
			displaySmall = MaterialTheme.typography.displaySmall.copy(),

			bodyLarge = MaterialTheme.typography.bodyLarge.copy(),
			bodyMedium = MaterialTheme.typography.bodyMedium.copy(),
			bodySmall = MaterialTheme.typography.bodySmall.copy(),

			labelLarge = MaterialTheme.typography.labelLarge.copy(),
			labelMedium = MaterialTheme.typography.labelMedium.copy(),
			labelSmall = MaterialTheme.typography.labelSmall.copy(),
		),
		content = content
	)
}
