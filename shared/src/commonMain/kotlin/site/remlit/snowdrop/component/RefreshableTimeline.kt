package site.remlit.snowdrop.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.IdentifiableObject
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.bg
import site.remlit.snowdrop.util.scrollingUpward
import site.remlit.snowdrop.view.ScrollEndCallback
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.nothing_to_see_here
import kotlin.collections.forEach

/**
 * Refreshable and infinitely scrollable timeline.
 *
 * @param fetchMethod Method following basic pagination requirements
 * @param timelineComponent Component to use for items in the timeline
 * @param refreshKey Mutable state that can be updated to refresh the timeline
 * */
@Composable
fun <T : IdentifiableObject<String>> RefreshableTimeline(
	fetchMethod: suspend (
			maxId: String?,
			minId: String?,
			sinceId: String?
		) -> ApiResponse<List<T>>,
	/**
	 * item: Item from timeline
	 * onUpdate: Function to return updated item to put in timeline list
	 * */
	timelineComponent: @Composable (
			item: T,
			onUpdate: (T) -> Unit
		) -> Unit,
	refreshKey: Int = 0,
	countTowardsScrollingUpward: Boolean = false
) {
	val coroutineScope = rememberCoroutineScope()

	val timeline = remember { mutableStateListOf<T>() }
	val refreshState = rememberPullToRefreshState()
	val listState = rememberLazyListState().also {
		it.ScrollEndCallback {
			coroutineScope.launch {
				if (timeline.isEmpty()) return@launch
				val res = fetchMethod(timeline.last().id, null, null)
				if (res.error) return@launch
				if (res.response == null) return@launch
				timeline.addAll(res.response)
			}
		}
	}

	var isRefreshing by remember { mutableStateOf(false) }

	suspend fun addOrUpdateTimeline() {
		isRefreshing = true
		val res = fetchMethod(null, null, null)
		if (res.error) return
		if (res.response == null) return
		timeline.clear()
		timeline.addAll(res.response)
		listState.scrollToItem(0)
		isRefreshing = false
	}

	fun updateOccurrencesOfItem(old: T, new: T) = bg {
		// not as bad as it once was but not great, probably
		if (old is Status && new is Status) {
			if (old == new) return@bg
			val tl = timeline as SnapshotStateList<Status>

			// todo: fix update on repeat of post not changing actual post
			tl.indices.forEach { i ->
				val current = tl[i]
				var updated: Status = new

				if (
					(current.id == old.id ||
						current.id == old.reblog?.id ||
						current.id == old.quote?.id ||
						current.id == old.quotedStatus?.id) &&
					current != updated
				) tl[i] = updated

				updated = current.copy(reblog = new)
				if (
					(current.reblog?.id == old.id) &&
					current != updated
				) tl[i] = updated

				updated = current.copy(quotedStatus = new, quote = new)
				if (
					(current.quotedStatus?.id == old.id ||
						current.quote?.id == old.id) &&
					current != updated
				) tl[i] = updated
			}
		} else {
			timeline[timeline.indexOf(old)] = new
		}
	}

	LaunchedEffect(refreshKey) { addOrUpdateTimeline() }

	PullToRefreshBox(
		isRefreshing = isRefreshing,
		state = refreshState,
		onRefresh = {
			coroutineScope.launch {
				coroutineScope.launch { addOrUpdateTimeline() }
				listState.scrollToItem(0)
			}
		}
	) {
		var timelineModifier = Modifier.fillMaxSize()

		if (countTowardsScrollingUpward) {
			// for determining if the compose FAB should be visible
			val nestedScrollConnection = remember {
				object : NestedScrollConnection {
					override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
						if (available.y < 0) scrollingUpward = false
						else if (available.y > 0) scrollingUpward = true

						return Offset.Zero
					}
				}
			}
			timelineModifier = timelineModifier
				.nestedScroll(nestedScrollConnection)
		}

		LazyColumn(
			state = listState,
			modifier = timelineModifier,
		) {
			if (timeline.isEmpty() && !isRefreshing) item {
				Column(
					modifier = Modifier.fillMaxHeight()
						.fillMaxWidth(),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center
				) {
					Text(
						stringResource(Res.string.nothing_to_see_here),
						modifier = Modifier.padding(vertical = 20.dp),
						fontStyle = FontStyle.Italic,
						fontSize = 13.sp,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
			} else items(
				items = timeline,
				key = { it.id }
			) {
				timelineComponent(it) { new -> updateOccurrencesOfItem(it, new) }
			}
		}
	}
}
