package site.remlit.snowdrop.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavOptions
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import site.remlit.snowdrop.Settings
import site.remlit.snowdrop.TimelineRoute
import site.remlit.snowdrop.api.timeline.getHomeTimeline
import site.remlit.snowdrop.component.Status
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.LocalNavController
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_settings_24px

@Composable
@Preview
fun TimelineView() = ViewSurface {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		val navHandler = LocalNavController.current
		val coroutineScope = rememberCoroutineScope()
		val listState = rememberLazyListState()

		var timeline = remember { mutableStateListOf<Status>() }
		var refreshState = rememberPullToRefreshState()
		var isRefreshing = remember { mutableStateOf(false) }

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
			title = {
				Text("Timeline")
			},
			actions = {
				IconButton(onClick = { navHandler.navigate(Settings) }) {
					Icon(painterResource(Res.drawable.icon_settings_24px), null)
				}
			}
		)

		PullToRefreshBox(
			isRefreshing = isRefreshing.value,
			state = refreshState,
			onRefresh = { coroutineScope.launch {
				coroutineScope.launch { addOrUpdateTimeline() }
				listState.scrollToItem(0)
			} }
		) {
			LazyColumn(
				state = listState,
				modifier = Modifier
					.fillMaxSize()
			) {
				items(
					items = timeline,
					key = { status ->
						status.id
					}
				) { status ->
					Status(status)
				}
			}
		}
	}
}