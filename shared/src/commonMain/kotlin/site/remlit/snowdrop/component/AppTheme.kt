package site.remlit.snowdrop.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.russhwolf.settings.ExperimentalSettingsApi
import site.remlit.snowdrop.util.settings

/** Platform specific light color scheme */
@Composable
expect fun getLightColorScheme(): ColorScheme

/** Platform specific dark color scheme */
@Composable
expect fun getDarkColorScheme(): ColorScheme


/**
 * Pre-configured MaterialTheme component.
 *
 * @param darkTheme If dark theme should be forced rather than following system settings
 * @param content Content of app
 *
 * @since 0.0.1-alpha
 * */
@OptIn(ExperimentalSettingsApi::class)
@Composable
fun AppTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	content: @Composable () -> Unit
) {
	// todo: allow forcing light/dark/black/system by settings
	val amoledBlack by remember { settings.getBooleanFlow("amoled_black", false) }
		.collectAsStateWithLifecycle(false)

	MaterialTheme(
		colorScheme = if (darkTheme && amoledBlack)
			getDarkColorScheme().copy(
				surface = Color(0,0,0,255), // top bar
				background = Color(0,0,0,255), // background
				surfaceContainer = Color(10,10,10,255) // navbar
			)
		else if (darkTheme) getDarkColorScheme() else getLightColorScheme(),

		// this isn't used, but allows tweaks to typography with ease whenever we may want to add them
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
