package site.remlit.snowdrop.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Basis of every View. Sets a standard background and fills the entire screen with its content.
 * @since 0.0.1-alpha
 * */
@Composable
fun ViewSurface(content: @Composable () -> Unit) {
	Column(
		modifier = Modifier.background(MaterialTheme.colorScheme.background)
			.fillMaxWidth().fillMaxHeight()
	) { content() }
}
