package site.remlit.snowdrop.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontStyle
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.IdentifiableObject
import site.remlit.snowdrop.util.SnackbarController
import site.remlit.snowdrop.util.scrollingUpward
import site.remlit.snowdrop.util.settings
import site.remlit.snowdrop.view.ScrollEndCallback
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.nothing_to_see_here

/**
 * Refreshable and infinitely scrollable timeline.
 *
 * @param fetchMethod Method following basic pagination requirements
 * @param timelineComponent Component to use for items in the timeline
 * @param refreshKey Mutable state that can be updated to refresh the timeline
 * */
@OptIn(ExperimentalSettingsApi::class)
@Composable
fun <T : IdentifiableObject<String>> RefreshableTimeline(
	fetchMethod: suspend (
			maxId: String?,
			minId: String?,
			sinceId: String?
		) -> ApiResponse<List<T>>,
	onRefresh: () -> Unit = {},
	timelineComponent: @Composable (item: T) -> Unit,
	leadingItem: @Composable () -> Unit = {},
	trailingItem: @Composable () -> Unit = {},
	modifier: Modifier = Modifier,
	itemModifier: Modifier = Modifier,
	refreshKey: Int = 0,
	scrollToTopPostRefresh: Boolean = true,
	countTowardsScrollingUpward: Boolean = false
) {
	val snackbarHandler = SnackbarController.current
	val coroutineScope = rememberCoroutineScope()

	val timeline = remember { mutableStateListOf<T>() }
	val refreshState = rememberPullToRefreshState()
	var isRefreshing by rememberSaveable { mutableStateOf(false) }

	val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
	listState.also {
		it.ScrollEndCallback {
			coroutineScope.launch {
				if (timeline.isEmpty()) return@launch
				val res = fetchMethod(timeline.last().id, null, null)
				if (res.error || res.response == null) {
					res.handleError(snackbarHandler)
					return@launch
				}
				timeline.addAll(res.response)
			}
		}
	}

	suspend fun addOrUpdateTimeline() {
		isRefreshing = true
		val res = fetchMethod(null, null, null)
		if (res.error) return
		if (res.response == null) return
		timeline.clear()
		timeline.addAll(res.response)
		if (scrollToTopPostRefresh) listState.scrollToItem(0)
		isRefreshing = false
	}

	LaunchedEffect(refreshKey) { addOrUpdateTimeline() }

	PullToRefreshBox(
		isRefreshing = isRefreshing,
		state = refreshState,
		onRefresh = {
			coroutineScope.launch {
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
					modifier = Modifier.fillMaxHeight()
						.fillMaxWidth(),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center
				) {
					Text(
						stringResource(Res.string.nothing_to_see_here),
						fontStyle = FontStyle.Italic
					)
				}
			} else items(
				items = timeline,
				key = { it.id }
			) {
				Box(modifier = itemModifier) {
					timelineComponent(it)
				}
			}

			item { trailingItem() }
		}
	}
}
