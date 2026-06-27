package site.remlit.snowdrop.util

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.staticCompositionLocalOf

val SnackbarController = staticCompositionLocalOf<SnackbarHostState> {
	error("SnackbarHostState not found")
}
