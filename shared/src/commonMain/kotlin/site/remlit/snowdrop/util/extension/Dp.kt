package site.remlit.snowdrop.util.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.toPixels(): Float {
	val dp = this
	with (LocalDensity.current) {
		return dp.toPx()
	}
}

@Composable
fun Dp.toPixelsRounded(): Int {
	val dp = this
	with (LocalDensity.current) {
		return dp.roundToPx()
	}
}

