package site.remlit.snowdrop.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import site.remlit.snowdrop.model.IdentifiableObject

/**
 * @since 0.0.7-alpha
 * */
@Composable
inline fun <reified T : IdentifiableObject<String>> rememberTimelineStateController() =
	remember { TimelineStateController<T>() }

data class TimelineStateController<T : IdentifiableObject<String>>(
	val timelines: SnapshotStateList<T> = mutableStateListOf()
)
