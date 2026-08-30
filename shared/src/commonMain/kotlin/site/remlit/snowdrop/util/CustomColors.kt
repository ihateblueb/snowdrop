package site.remlit.snowdrop.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun LikeColor() = if (isSystemInDarkTheme()) Color(color = 0xFFFFD056) else
	Color(color = 0xFFD99C00)

@Composable
fun BoostColor() = if (isSystemInDarkTheme()) Color(color = 0xFF3FE741) else
	Color(color = 0xFF00A502)


val WarningColor = Color(color = 0xFFFFAF25)
val WarningColor25 = Color(color = 0x40FFAF25)
