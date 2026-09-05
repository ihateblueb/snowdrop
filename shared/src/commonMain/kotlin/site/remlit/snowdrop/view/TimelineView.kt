package site.remlit.snowdrop.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.SettingsRoute
import site.remlit.snowdrop.api.getBookmarks
import site.remlit.snowdrop.api.timeline.getBubbleTimeline
import site.remlit.snowdrop.api.timeline.getHomeTimeline
import site.remlit.snowdrop.api.timeline.getListTimeline
import site.remlit.snowdrop.api.timeline.getPublicTimeline
import site.remlit.snowdrop.component.RefreshableTimeline
import site.remlit.snowdrop.component.Status
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.component.dropdown.MenuDivider
import site.remlit.snowdrop.component.dropdown.PreparedDropdownMenu
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.LocalSnackbarController
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.cache.fetchLists
import site.remlit.snowdrop.util.extension.getPreparedDropdownMenuItemShape
import site.remlit.snowdrop.util.getFeature
import site.remlit.snowdrop.util.settings
import site.remlit.snowdrop.util.vibrate
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.bookmarks
import snowdrop.shared.generated.resources.bubble
import snowdrop.shared.generated.resources.global
import snowdrop.shared.generated.resources.home
import snowdrop.shared.generated.resources.icon_bookmark_24px
import snowdrop.shared.generated.resources.icon_bubble_chart_24px
import snowdrop.shared.generated.resources.icon_chevron_right_24px
import snowdrop.shared.generated.resources.icon_globe_24px
import snowdrop.shared.generated.resources.icon_home_24px
import snowdrop.shared.generated.resources.icon_keyboard_arrow_down_24px
import snowdrop.shared.generated.resources.icon_keyboard_arrow_up_24px
import snowdrop.shared.generated.resources.icon_list_24px
import snowdrop.shared.generated.resources.icon_lock_24px
import snowdrop.shared.generated.resources.icon_lock_open_right_24px
import snowdrop.shared.generated.resources.icon_map_24px
import snowdrop.shared.generated.resources.icon_more_vert_24px
import snowdrop.shared.generated.resources.icon_settings_24px
import snowdrop.shared.generated.resources.list
import snowdrop.shared.generated.resources.lists
import snowdrop.shared.generated.resources.local
import snowdrop.shared.generated.resources.lock
import snowdrop.shared.generated.resources.lock_timeline
import snowdrop.shared.generated.resources.lock_timeline_description
import snowdrop.shared.generated.resources.more
import snowdrop.shared.generated.resources.ok
import snowdrop.shared.generated.resources.settings
import snowdrop.shared.generated.resources.this_popup_wont_appear_again
import snowdrop.shared.generated.resources.timeline_dropdown_menu
import snowdrop.shared.generated.resources.unlock

@OptIn(ExperimentalSettingsApi::class)
@Composable
fun TimelineView() = ViewSurface {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		val navHandler = LocalNavController.current
		val snackbarController = LocalSnackbarController.current
		val haptics = LocalHapticFeedback.current
		val coroutineScope = rememberCoroutineScope()

		val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

		var refreshKey by rememberSaveable { mutableStateOf(0) }

		val timelineLocked by remember { settings.getBooleanFlow("timeline_locked", false) }
			.collectAsStateWithLifecycle(false)
		val timelineLockedPromptSeen by remember { settings.getBooleanFlow("timeline_locked_prompt_seen", false) }
			.collectAsStateWithLifecycle(false)

		val lists by remember { fetchLists(snackbarController) }
			.collectAsStateWithLifecycle(null)

		// 0 - home, 1 - local, 2 - bubble, 3 - global, 4 - bookmarks, 5 - list
		val timelineType by settings.getIntFlow("timeline", 0)
			.distinctUntilChanged()
			.collectAsStateWithLifecycle(0)
		val listId by settings.getStringFlow("timeline_list_id", "")
			.distinctUntilChanged()
			.collectAsStateWithLifecycle("")

		var timelinePickerOpen by remember { mutableStateOf(false) }
		var listPickerOpen by remember { mutableStateOf(false) }

		//<editor-fold name="getTimeline method">
		suspend fun getTimeline(
			maxId: String? = null,
			minId: String? = null,
			sinceId: String? = null
		): ApiResponse<List<Status>> {
			return when (timelineType) {
				0 -> getHomeTimeline(maxId = maxId, minId = minId, sinceId = sinceId)
				1 -> getPublicTimeline(maxId = maxId, minId = minId, sinceId = sinceId, local = true)
				2 -> if (getFeature("bubble_timeline_akkoma")) getBubbleTimeline(maxId = maxId, minId = minId, sinceId = sinceId)
				else getPublicTimeline(maxId = maxId, minId = minId, sinceId = sinceId, bubble = true)
				3 -> getPublicTimeline(maxId = maxId, minId = minId, sinceId = sinceId, remote = true)

				4 -> getBookmarks(maxId = maxId, minId = minId, sinceId = sinceId)
				5 -> getListTimeline(list = listId, maxId = maxId, minId = minId, sinceId = sinceId)

				else -> throw IllegalArgumentException("Invalid timeline type $timelineType")
			}
		}
		//</editor-fold>

		//<editor-fold name="Timeline icon">
		@Composable
		fun RenderTimelineTypeIcon(type: Int? = null) {
			when (type ?: timelineType) {
				0 -> Icon(painterResource(Res.drawable.icon_home_24px), null)
				1 -> Icon(painterResource(Res.drawable.icon_map_24px), null)
				2 -> Icon(painterResource(Res.drawable.icon_bubble_chart_24px), null)
				3 -> Icon(painterResource(Res.drawable.icon_globe_24px), null)

				4 -> Icon(painterResource(Res.drawable.icon_bookmark_24px), null)
				5 -> Icon(painterResource(Res.drawable.icon_list_24px), null)
			}
		}
		//</editor-fold>

		//<editor-fold name="Timeline selection dropdown">
		fun changeTimeline(timeline: Int) {
			blockingSettings.putInt("timeline", timeline)
			vibrate(true, haptics)
			timelinePickerOpen = false
			refreshKey++
		}

		@Composable
		fun RenderTimelineSelectionDropdown() {
			PreparedDropdownMenu(
				expanded = timelinePickerOpen,
				onDismissRequest = { timelinePickerOpen = false },
				offset = DpOffset(0.dp, 15.dp)
			) {
				DropdownMenuItem(
					leadingIcon = { RenderTimelineTypeIcon(0) },
					text = { Text(stringResource(Res.string.home)) },
					shape = MenuDefaults.leadingItemShape,
					onClick = { changeTimeline(0) }
				)

				DropdownMenuItem(
					leadingIcon = { RenderTimelineTypeIcon(1) },
					text = { Text(stringResource(Res.string.local)) },
					shape = MenuDefaults.middleItemShape,
					onClick = { changeTimeline(1) }
				)

				if (getFeature("bubble_timeline") || getFeature("bubble_timeline_akkoma"))
					DropdownMenuItem(
						leadingIcon = { RenderTimelineTypeIcon(2) },
						text = { Text(stringResource(Res.string.bubble)) },
						shape = MenuDefaults.middleItemShape,
						onClick = { changeTimeline(2) }
					)

				DropdownMenuItem(
					leadingIcon = { RenderTimelineTypeIcon(3) },
					text = { Text(stringResource(Res.string.global)) },
					shape = MenuDefaults.middleItemShape,
					onClick = { changeTimeline(3) }
				)

				MenuDivider()

				DropdownMenuItem(
					leadingIcon = { RenderTimelineTypeIcon(4) },
					text = { Text(stringResource(Res.string.bookmarks)) },
					shape = MenuDefaults.middleItemShape,
					onClick = { changeTimeline(4) }
				)

				//<editor-fold name="List menu">
				Box {
					DropdownMenuItem(
						leadingIcon = { RenderTimelineTypeIcon(5) },
						trailingIcon = {
							Icon(
								painterResource(Res.drawable.icon_chevron_right_24px),
								null
							)
						},
						text = { Text(stringResource(Res.string.lists)) },
						shape = MenuDefaults.trailingItemShape,
						onClick = {
							listPickerOpen = !listPickerOpen
							vibrate(true, haptics)
						}
					)

					PreparedDropdownMenu(
						expanded = listPickerOpen,
						onDismissRequest = { listPickerOpen = false }
					) {
						lists?.forEachIndexed { index, list ->
							DropdownMenuItem(
								onClick = {
									blockingSettings.putString("timeline_list_id", list.id)
									blockingSettings.putInt("timeline", 5)
									vibrate(true, haptics)
									listPickerOpen = false
									timelinePickerOpen = false
								},
								shape = lists.getPreparedDropdownMenuItemShape(index),
								text = { Text(list.title) }
							)
						}
					}
				}
				//</editor-fold>
			}
		}
		//</editor-fold>


		/*
		 * Actual Timeline View
		 */
		//<editor-fold name="Top bar">
		TopAppBar(
			navigationIcon = {
				Box(
					modifier = Modifier.minimumInteractiveComponentSize()
						.size(IconButtonDefaults.smallContainerSize()),
					contentAlignment = Alignment.Center,
				) {
					RenderTimelineTypeIcon()
				}
			},
			title = {
				val __translation_home = stringResource(Res.string.home)
				val __translation_local = stringResource(Res.string.local)
				val __translation_bubble = stringResource(Res.string.bubble)
				val __translation_global = stringResource(Res.string.global)
				val __translation_bookmarks = stringResource(Res.string.bookmarks)
				val __translation_list = stringResource(Res.string.list)

				val __translate_timeline_dropdown_menu = stringResource(Res.string.timeline_dropdown_menu)

				val __translation = remember(timelineType) {
					when (timelineType) {
						0 -> __translation_home
						1 -> __translation_local
						2 -> __translation_bubble
						3 -> __translation_global

						4 -> __translation_bookmarks
						5 -> lists?.first { it.id == listId }?.title ?: __translation_list
						else -> ""
					}
				}

				Row(
					horizontalArrangement = Arrangement.spacedBy(10.dp),
					verticalAlignment = Alignment.CenterVertically,

					modifier = Modifier.clickable(
						interactionSource = MutableInteractionSource(),
						indication = null,
						onClick = {
							timelinePickerOpen = !timelinePickerOpen
						}
					).semantics {
						contentDescription = __translate_timeline_dropdown_menu
					}
				) {
					Text(__translation)

					if (timelinePickerOpen) Icon(painterResource(Res.drawable.icon_keyboard_arrow_up_24px), null)
					else Icon(painterResource(Res.drawable.icon_keyboard_arrow_down_24px), null)

					RenderTimelineSelectionDropdown()
				}
			},
			actions = {
				val __translation_more = stringResource(Res.string.more)
				val __translation_settings = stringResource(Res.string.settings)

				var showDropdown by remember { mutableStateOf(false) }
				IconButton(
					onClick = { showDropdown = !showDropdown },
					modifier = Modifier.semantics { contentDescription = __translation_more }
				) {
					Icon(painterResource(Res.drawable.icon_more_vert_24px), null)
				}

				PreparedDropdownMenu(
					expanded = showDropdown,
					onDismissRequest = { showDropdown = !showDropdown }
				) {
					DropdownMenuItem(
						leadingIcon = {
							if (timelineLocked) Icon(painterResource(Res.drawable.icon_lock_open_right_24px), null)
							else Icon(painterResource(Res.drawable.icon_lock_24px), null)
						},
						text = {
							if (timelineLocked) Text(stringResource(Res.string.unlock))
							else Text(stringResource(Res.string.lock))
						},
						shape = MenuDefaults.standaloneItemShape,
						onClick = {
							blockingSettings.putBoolean("timeline_locked", !timelineLocked)
							showDropdown = false
						}
					)
				}

				// settings
				IconButton(
					onClick = { navHandler.navigate(SettingsRoute) },
					modifier = Modifier.semantics { contentDescription = __translation_settings }
				) {
					Icon(painterResource(Res.drawable.icon_settings_24px), null)
				}
			},
			modifier = Modifier.clickable(
				interactionSource = MutableInteractionSource(),
				indication = null,
				onClick = {
					coroutineScope.launch {
						listState.animateScrollToItem(0)
					}
				}
			),
		)
		//</editor-fold>

		//<editor-fold name="Lock timeline info popup">
		if (timelineLocked && !timelineLockedPromptSeen) {
			AlertDialog(
				title = { Text(stringResource(Res.string.lock_timeline)) },
				text = {
					Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
						Text(stringResource(Res.string.lock_timeline_description))
						Text(stringResource(Res.string.this_popup_wont_appear_again))
					}
				},
				onDismissRequest = { blockingSettings.putBoolean("timeline_locked_prompt_seen", true) },
				confirmButton = {
					TextButton(
						onClick = { blockingSettings.putBoolean("timeline_locked_prompt_seen", true) }
					) {
						Text(stringResource(Res.string.ok))
					}
				},
				properties = DialogProperties(
					dismissOnBackPress = true,
					dismissOnClickOutside = true
				)
			)
		}
		//</editor-fold>

		RefreshableTimeline(
			fetchMethod = { maxId, minId, sinceId -> getTimeline(maxId, minId, sinceId) },
			timelineComponent = { item, onUpdate -> Status(item, onUpdate, lockable = true) },
			refreshKey = refreshKey,
			countTowardsScrollingUpward = true,
			listState = listState
		)
	}
}
