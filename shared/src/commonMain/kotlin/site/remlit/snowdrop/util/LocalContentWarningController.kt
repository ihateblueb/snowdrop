package site.remlit.snowdrop.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Create a remembered content warning controller
 *
 * @since 0.0.6
 * */
@Composable
fun rememberContentWarningController() = remember { ContentWarningController() }

/**
 * Content warning controller. Remembers the states of content warnings of a current view.
 *
 * @since 0.0.6-alpha
 * */
data class ContentWarningController(
	val state: SnapshotStateMap<String, Boolean> = mutableStateMapOf<String, Boolean>()
) {
	var defaultValue: Boolean = false

	/**
	 * Sets all content warning states to a value
	 *
	 * @param value Value to set states to
	 *
	 * @since 0.0.6-alpha
	 * */
	fun setAll(value: Boolean) {
		state.forEach { (key, _) -> state[key] = value }
	}
}

val LocalContentWarningController = staticCompositionLocalOf<ContentWarningController> {
	error("ContentWarningController not found")
}
