package site.remlit.snowdrop.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.russhwolf.settings.ExperimentalSettingsApi

@OptIn(ExperimentalSettingsApi::class)
@Composable
fun LikeColor(): Color {
	val favoriteButtonIcon by settings.getIntFlow("favorite_button_icon", 0)
		.collectAsStateWithLifecycle(blockingSettings.getInt("favorite_button_icon", 0))

	return when (favoriteButtonIcon) {
		0 -> if (isSystemInDarkTheme()) Color(color = 0xFFFFD056) else
			Color(color = 0xFFD99C00)
		else -> if (isSystemInDarkTheme()) Color(color = 0xFFFF4A4A) else
			Color(color = 0xFFD72929)
	}
}

@Composable
fun BoostColor() = if (isSystemInDarkTheme()) Color(color = 0xFF3FE741) else
	Color(color = 0xFF00A502)


val WarningColor = Color(color = 0xFFFFAF25)
val WarningColor25 = Color(color = 0x40FFAF25)
