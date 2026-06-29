package site.remlit.snowdrop.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.SettingsRoute
import site.remlit.snowdrop.api.timeline.getHomeTimeline
import site.remlit.snowdrop.api.timeline.getPublicTimeline
import site.remlit.snowdrop.component.RefreshableTimeline
import site.remlit.snowdrop.component.Status
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.getFeature
import site.remlit.snowdrop.util.settings
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.bubble
import snowdrop.shared.generated.resources.global
import snowdrop.shared.generated.resources.home
import snowdrop.shared.generated.resources.icon_bubble_chart_24px
import snowdrop.shared.generated.resources.icon_globe_24px
import snowdrop.shared.generated.resources.icon_home_24px
import snowdrop.shared.generated.resources.icon_keyboard_arrow_down_24px
import snowdrop.shared.generated.resources.icon_keyboard_arrow_up_24px
import snowdrop.shared.generated.resources.icon_map_24px
import snowdrop.shared.generated.resources.icon_settings_24px
import snowdrop.shared.generated.resources.local

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

@OptIn(ExperimentalSettingsApi::class)
@Composable
fun TimelineView() = ViewSurface {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		val navHandler = LocalNavController.current

		// 0 - home, 1 - local, 2 - bubble, 3 - global
		val timelineType by settings.getIntFlow("timeline", 0)
			.collectAsStateWithLifecycle(0)
		var timelinePickerOpen by remember { mutableStateOf(false) }

		suspend fun getTimeline(
			maxId: String? = null,
			minId: String? = null,
			sinceId: String? = null
		): ApiResponse<List<Status>> {
			return when (timelineType) {
				0 -> getHomeTimeline(maxId = maxId, minId = minId, sinceId = sinceId)
				1 -> getPublicTimeline(maxId = maxId, minId = minId, sinceId = sinceId, local = true)
				2 -> getPublicTimeline(maxId = maxId, minId = minId, sinceId = sinceId, remote = true) // todo: fix bubble timeline
				else -> getPublicTimeline(maxId = maxId, minId = minId, sinceId = sinceId, remote = true) // else also 3
			}
		}

		@Composable
		fun RenderTimelineTypeIcon(type: Int? = null) {
			when (type ?: timelineType) {
				0 -> Icon(painterResource(Res.drawable.icon_home_24px), null)
				1 -> Icon(painterResource(Res.drawable.icon_map_24px), null)
				2 -> Icon(painterResource(Res.drawable.icon_bubble_chart_24px), null)
				3 -> Icon(painterResource(Res.drawable.icon_globe_24px), null)
			}
		}

		@Composable
		fun RenderTimelineSelectionDropdown() {
			DropdownMenu(
				expanded = timelinePickerOpen,
				onDismissRequest = { timelinePickerOpen = false },
			) {
				DropdownMenuItem(
					leadingIcon = { RenderTimelineTypeIcon(0) },
					text = { Text(stringResource(Res.string.home)) },
					onClick = { blockingSettings.putInt("timeline", 0); timelinePickerOpen = false }
				)
				DropdownMenuItem(
					leadingIcon = { RenderTimelineTypeIcon(1) },
					text = { Text(stringResource(Res.string.local)) },
					onClick = { blockingSettings.putInt("timeline", 1); timelinePickerOpen = false }
				)
				if (getFeature("bubble_timeline"))
					DropdownMenuItem(
						leadingIcon = { RenderTimelineTypeIcon(2) },
						text = { Text(stringResource(Res.string.bubble)) },
						onClick = { blockingSettings.putInt("timeline", 2); timelinePickerOpen = false }
					)
				DropdownMenuItem(
					leadingIcon = { RenderTimelineTypeIcon(3) },
					text = { Text(stringResource(Res.string.global)) },
					onClick = { blockingSettings.putInt("timeline", 3); timelinePickerOpen = false }
				)
			}
		}


		/*
		 *
		 * Actual Timeline View
		 *
		 */
		TopAppBar(
			modifier = Modifier.clickable(
				interactionSource = MutableInteractionSource(),
				indication = null,
				onClick = {
					/*
					coroutineScope.launch {
						listState.animateScrollToItem(0)
					}
					*/

					timelinePickerOpen = !timelinePickerOpen
				}
			),
			title = {
				Row(
					horizontalArrangement = Arrangement.spacedBy(10.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					RenderTimelineTypeIcon()

					when (timelineType) {
						0 -> Text(stringResource(Res.string.home))
						1 -> Text(stringResource(Res.string.local))
						2 -> Text(stringResource(Res.string.bubble))
						3 -> Text(stringResource(Res.string.global))
					}

					if (timelinePickerOpen) Icon(painterResource(Res.drawable.icon_keyboard_arrow_up_24px), null)
					else Icon(painterResource(Res.drawable.icon_keyboard_arrow_down_24px), null)

					RenderTimelineSelectionDropdown()
				}
			},
			actions = {
				IconButton(onClick = { navHandler.navigate(SettingsRoute) }) {
					Icon(painterResource(Res.drawable.icon_settings_24px), null)
				}
			}
		)

		RefreshableTimeline(
			fetchMethod = { maxId, minId, sinceId -> getTimeline(maxId, minId, sinceId) },
			timelineComponent = { Status(it) },
			refreshKey = timelineType,
			countTowardsScrollingUpward = true
		)
	}
}
