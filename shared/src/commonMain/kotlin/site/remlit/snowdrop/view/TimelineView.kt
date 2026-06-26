package site.remlit.snowdrop.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import site.remlit.snowdrop.SettingsRoute
import site.remlit.snowdrop.api.timeline.getHomeTimeline
import site.remlit.snowdrop.component.Status
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.scrollingUpward
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_settings_24px

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

@Composable
fun TimelineView() = ViewSurface {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		val navHandler = LocalNavController.current
		val coroutineScope = rememberCoroutineScope()

		val timeline = remember { mutableStateListOf<Status>() }
		val refreshState = rememberPullToRefreshState()
		val listState = rememberLazyListState().also {
			it.ScrollEndCallback {
				coroutineScope.launch {
					val res = getHomeTimeline(maxId = timeline.last().id)
					if (res.error) return@launch
					if (res.response == null) return@launch
					timeline.addAll(res.response)
				}
			}
		}
		val isRefreshing = remember { mutableStateOf(false) }

		suspend fun addOrUpdateTimeline() {
			isRefreshing.value = true
			val res = getHomeTimeline()
			if (res.error) return
			if (res.response == null) return
			timeline.clear()
			timeline.addAll(res.response)
			listState.scrollToItem(0)
			isRefreshing.value = false
		}

		LaunchedEffect(Unit) {
			addOrUpdateTimeline()
		}

		TopAppBar(
			modifier = Modifier.clickable(
				interactionSource = MutableInteractionSource(),
				indication = null,
				onClick = {
					coroutineScope.launch {
						listState.animateScrollToItem(0)
					}
				}
			),
			title = {
				Text("Timeline")
			},
			actions = {
				IconButton(onClick = { navHandler.navigate(SettingsRoute) }) {
					Icon(painterResource(Res.drawable.icon_settings_24px), null)
				}
			}
		)

		PullToRefreshBox(
			isRefreshing = isRefreshing.value,
			state = refreshState,
			onRefresh = {
				coroutineScope.launch {
					coroutineScope.launch { addOrUpdateTimeline() }
					listState.scrollToItem(0)
				}
			}
		) {
			val nestedScrollConnection = remember {
				object : NestedScrollConnection {
					override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
						if (available.y < 0) scrollingUpward = false
						else if (available.y > 0) scrollingUpward = true

						return Offset.Zero
					}
				}
			}

			LazyColumn(
				state = listState,
				modifier = Modifier
					.fillMaxSize()
					.nestedScroll(nestedScrollConnection),
			) {
				items(
					items = timeline,
					key = { status ->
						status.id!!
					}
				) { status ->
					Status(status)
				}
			}
		}
	}
}
