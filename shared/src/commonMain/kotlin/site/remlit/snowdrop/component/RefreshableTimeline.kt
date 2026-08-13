package site.remlit.snowdrop.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.IdentifiableObject
import site.remlit.snowdrop.model.viewModel.TimelineViewModel
import site.remlit.snowdrop.model.viewModel.timelineViewModelFactory
import site.remlit.snowdrop.util.LocalSnackbarController
import site.remlit.snowdrop.util.log.debug
import site.remlit.snowdrop.util.scrollingUpward
import site.remlit.snowdrop.util.update
import site.remlit.snowdrop.util.vibrateSoft
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.nothing_to_see_here

//<editor-fold name="ScrollEndCallback">
@Composable
inline fun LazyListState.ScrollEndCallback(crossinline callback: () -> Unit) {
	val postsTillEndBeforeFetch = 10

	LaunchedEffect(key1 = this) {
		snapshotFlow { layoutInfo }
			.filter { it.totalItemsCount > 0 }
			.map { it.totalItemsCount - (it.visibleItemsInfo.lastOrNull()?.index ?: -1) <= postsTillEndBeforeFetch }
			.distinctUntilChanged()
			.filter { it }
			.onEach { callback() }
			.collect()
	}
}
//</editor-fold>

/**
 * Refreshable and infinitely scrollable timeline.
 *
 * @param modifier Modifier for PullToRefreshBox
 * @param viewModelKey Key for timelineViewModel
 * @param timelineViewModel View model for storing persistent timeline data
 * @param fetchMethod Method following basic pagination requirements
 * @param onRefresh Called upon refresh of the timeline
 * @param timelineComponent Component to use for items in the timeline, must accept T as first parameter
 * @param leadingItem Item leading before the timeline content
 * @param trailingItem Item trailing after the timeline content
 * @param itemModifier Modifier for each timeline item's Box
 * @param refreshKey Mutable state that can be updated to refresh the timeline
 * @param scrollToTopPostRefresh If the timeline should scroll to top after refreshing
 * @param countTowardsScrollingUpward If scrolling should be observed for the compose post FAB, usually no
 * @param distinctCheck If timeline should remove duplicate elements before rendering, necessary for certain endpoints unfortunately
 *
 * @sample site.remlit.snowdrop.view.NotificationsView
 * @since 0.0.2-alpha
 * */
@Composable
fun <T : IdentifiableObject<String>> RefreshableTimeline(
	modifier: Modifier = Modifier,

	viewModelKey: String? = null,
	timelineViewModel: TimelineViewModel<T> = viewModel(
			key = viewModelKey,
			factory = timelineViewModelFactory<T>()
		),

	fetchMethod: suspend (
			maxId: String?,
			minId: String?,
			sinceId: String?
		) -> ApiResponse<List<T>>,
	onRefresh: () -> Unit = {},

	timelineComponent: @Composable (
		item: T,
		onUpdate: ((T?) -> Unit)
	) -> Unit,

	leadingItem: @Composable () -> Unit = {},
	trailingItem: @Composable () -> Unit = {},
	itemModifier: Modifier = Modifier,
	refreshKey: Any = 0,
	scrollToTopPostRefresh: Boolean = true,
	countTowardsScrollingUpward: Boolean = false,
	distinctCheck: Boolean = false,
) {
	val snackbarHandler = LocalSnackbarController.current
	val haptics = LocalHapticFeedback.current
	val coroutineScope = rememberCoroutineScope()

	// todo: make rememberSaveable

	val timeline = remember { timelineViewModel.timelineItems }
	val refreshState = rememberPullToRefreshState() // this is rememberSaveable
	var isRefreshing by rememberSaveable { mutableStateOf(false) }
	var isFetchingMore by rememberSaveable { mutableStateOf(false) }

	suspend fun addToTimeline() {
		if (timeline.isEmpty()) return
		isFetchingMore = true
		val res = fetchMethod(timeline.last().id, null, null)
		if (res.error || res.response == null) {
			res.handleError(snackbarHandler)
			return
		}
		timeline.addAll(res.response)
		isFetchingMore = false
	}

	val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
	listState.also {
		it.ScrollEndCallback {
			if (!isFetchingMore) coroutineScope.launch { addToTimeline() }
		}
	}

	suspend fun addOrUpdateTimeline() {
		debug { "(RefreshableTimeline) addOrUpdateTimeline called" }
		scrollingUpward = true
		isRefreshing = true
		val res = fetchMethod(null, null, null)
		if (res.error) {
			res.handleError(snackbarHandler)
			return
		}
		if (res.response == null) return
		timeline.clear()
		timeline.addAll(res.response)
		if (scrollToTopPostRefresh) listState.scrollToItem(0)
		isRefreshing = false
	}

	var initialized by rememberSaveable { mutableStateOf(false) }
	LaunchedEffect(Unit) {
		if (!initialized) addOrUpdateTimeline(); onRefresh(); initialized = true
	}

	var previousRefreshKey by rememberSaveable { mutableStateOf(refreshKey) }
	LaunchedEffect(refreshKey) {
		if (refreshKey != previousRefreshKey) addOrUpdateTimeline(); onRefresh()
		previousRefreshKey = refreshKey
	}

	PullToRefreshBox(
		isRefreshing = isRefreshing,
		state = refreshState,
		onRefresh = {
			coroutineScope.launch {
				vibrateSoft(haptics)
				coroutineScope.launch { addOrUpdateTimeline() }
				if (scrollToTopPostRefresh) listState.scrollToItem(0)
			}

			onRefresh()
		},
		modifier = modifier
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
			modifier = timelineModifier.then(modifier)
		) {
			item { leadingItem() }

			if (timeline.isEmpty() && !isRefreshing) item {
				Column(
					modifier = Modifier.fillMaxSize().padding(vertical = 20.dp),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center
				) {
					Text(
						stringResource(Res.string.nothing_to_see_here),
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						fontSize = 13.sp
					)
				}
			} else if (!isRefreshing) items(
				items = if (distinctCheck) timeline.distinctBy { it.id } else timeline,
				key = { it.id }
			) {
				Box(modifier = itemModifier) {
					timelineComponent(it) { new -> timeline.update(it, new) }
				}
			}

			if (!isRefreshing && isFetchingMore) item {
				Row(
					modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.Center
				) {
					CircularProgressIndicator()
				}
			}

			item { trailingItem() }
		}
	}
}
