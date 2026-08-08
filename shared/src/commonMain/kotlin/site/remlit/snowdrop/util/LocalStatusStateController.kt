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
fun rememberStatusStateController() = remember { StatusStateController() }

/**
 * Status state controller. Remembers the states of content warnings and filters for a view.
 *
 * @since 0.0.6-alpha
 * */
data class StatusStateController(
	/** When true, content warned status is VISIBLE */
	val cw: SnapshotStateMap<String, Boolean> = mutableStateMapOf<String, Boolean>(),
	/** When true, filtered status is VISIBLE */
	val filtered: SnapshotStateMap<String, Boolean> = mutableStateMapOf<String, Boolean>()
) {
	var defaultCwValue: Boolean = false
	var defaultFilteredValue: Boolean = false

	/**
	 * Sets all content warning states to a value
	 *
	 * @param value Value to set states to
	 *
	 * @since 0.0.6-alpha
	 * */
	fun setAllCw(value: Boolean) {
		cw.forEach { (key, _) -> cw[key] = value }
	}

	/**
	 * Sets all filtered states to a value
	 *
	 * @param value Value to set states to
	 *
	 * @since 0.0.6-alpha
	 * */
	fun setAllFiltered(value: Boolean) {
		filtered.forEach { (key, _) -> filtered[key] = value }
	}
}

val LocalStatusStateController = staticCompositionLocalOf<StatusStateController> {
	error("StatusStateController not found")
}
