package site.remlit.snowdrop.component

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import site.remlit.snowdrop.util.blockingSettings

@Composable
fun borderOnBackgroundColor() = if (blockingSettings.getBoolean("amoled_black", false))
	MaterialTheme.colorScheme.surfaceBright else MaterialTheme.colorScheme.surfaceContainer

@Composable
fun Divider() {
	HorizontalDivider(
		thickness = 1.dp,
		color = borderOnBackgroundColor()
	)
}
