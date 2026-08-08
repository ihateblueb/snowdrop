package site.remlit.snowdrop.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import site.remlit.snowdrop.util.LocalStatusStateController
import site.remlit.snowdrop.util.rememberStatusStateController

/**
 * Basis of every View. Sets a standard background and fills the entire screen with its content.
 * Provides a StatusStateController.
 *
 * @since 0.0.1-alpha
 * */
@Composable
fun ViewSurface(content: @Composable () -> Unit) {
	val statusStateController = rememberStatusStateController()
	CompositionLocalProvider(LocalStatusStateController provides statusStateController) {
		Column(
			modifier = Modifier.background(MaterialTheme.colorScheme.background)
				.fillMaxSize()
		) {
			content()
		}
	}
}
