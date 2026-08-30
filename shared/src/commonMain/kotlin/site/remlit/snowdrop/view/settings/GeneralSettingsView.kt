@file:Suppress("DEPRECATION")

package site.remlit.snowdrop.view.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.russhwolf.settings.ExperimentalSettingsApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.component.NavigationBackButton
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.component.Visibility
import site.remlit.snowdrop.util.ListItemShape
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.getDefaultVisibility
import site.remlit.snowdrop.util.listItemClip
import site.remlit.snowdrop.util.putDefaultVisibility
import site.remlit.snowdrop.util.settings
import site.remlit.snowdrop.util.translation
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.append_re_on_reply_content_warnings
import snowdrop.shared.generated.resources.default_post_visibility
import snowdrop.shared.generated.resources.general
import snowdrop.shared.generated.resources.haptics
import snowdrop.shared.generated.resources.icon_arrow_back_24
import snowdrop.shared.generated.resources.icon_keyboard_arrow_down_24px
import snowdrop.shared.generated.resources.icon_keyboard_arrow_up_24px
import snowdrop.shared.generated.resources.lock_timeline
import snowdrop.shared.generated.resources.lock_timeline_short_description
import snowdrop.shared.generated.resources.notifs_per_page
import snowdrop.shared.generated.resources.number_of_recent_emojis_to_save
import snowdrop.shared.generated.resources.posts_per_page
import snowdrop.shared.generated.resources.visibility_direct
import snowdrop.shared.generated.resources.visibility_followers
import snowdrop.shared.generated.resources.visibility_public
import snowdrop.shared.generated.resources.visibility_unlisted
import kotlin.math.roundToInt

@OptIn(ExperimentalSettingsApi::class)
@Composable
fun GeneralSettingsView() = ViewSurface {
	val navHandler = LocalNavController.current

	TopAppBar(
		navigationIcon = { NavigationBackButton() },
		title = {
			Text(stringResource(Res.string.general))
		}
	)

	LazyColumn(
		modifier = Modifier.padding(horizontal = 10.dp)
	) {
		item {
			val defaultVisibility by remember { getDefaultVisibility() }
				.collectAsStateWithLifecycle("public")

			var showVisibilityPicker by remember { mutableStateOf(false) }

			Card(
				modifier = Modifier.listItemClip(0, 1).padding(bottom = 10.dp),
				shape = ListItemShape(0, 1),
			) {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.default_post_visibility)) },
					trailingContent = {
						Row(
							horizontalArrangement = Arrangement.spacedBy(10.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							Visibility(defaultVisibility, true)

							if (showVisibilityPicker) Icon(painterResource(Res.drawable.icon_keyboard_arrow_up_24px), null)
							else Icon(painterResource(Res.drawable.icon_keyboard_arrow_down_24px), null)
						}
					},
					modifier = Modifier.clickable {
						showVisibilityPicker = !showVisibilityPicker
					},
					colors = ListItemDefaults.colors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
				)
			}
			AnimatedVisibility(
				visible = showVisibilityPicker,
				enter = dropdownEnterAnimation,
				exit = dropdownExitAnimation
			) {
				Column(
					modifier = Modifier.padding(horizontal = 10.dp).padding(bottom = 10.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh)
				) {
					Row(
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier.fillMaxWidth().height(42.dp)
							.selectable(
								selected = defaultVisibility == "public",
								role = Role.RadioButton,
								onClick = { putDefaultVisibility("public") }
							)
					) {
						RadioButton(
							selected = defaultVisibility == "public",
							onClick = null,
							modifier = Modifier.padding(start = 10.dp)
						)
						Text(
							stringResource(Res.string.visibility_public),
							modifier = Modifier.padding(start = 20.dp)
						)
					}
					Row(
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier.fillMaxWidth().height(42.dp)
							.selectable(
								selected = defaultVisibility == "unlisted",
								role = Role.RadioButton,
								onClick = { putDefaultVisibility("unlisted") }
							)
					) {
						RadioButton(
							selected = defaultVisibility == "unlisted",
							onClick = null,
							modifier = Modifier.padding(start = 10.dp)
						)
						Text(
							stringResource(Res.string.visibility_unlisted),
							modifier = Modifier.padding(start = 20.dp)
						)
					}
					Row(
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier.fillMaxWidth().height(42.dp)
							.selectable(
								selected = defaultVisibility == "private",
								role = Role.RadioButton,
								onClick = { putDefaultVisibility("private") }
							)
					) {
						RadioButton(
							selected = defaultVisibility == "private",
							onClick = null,
							modifier = Modifier.padding(start = 10.dp)
						)
						Text(
							stringResource(Res.string.visibility_followers),
							modifier = Modifier.padding(start = 20.dp)
						)
					}
					Row(
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier.fillMaxWidth().height(42.dp)
							.selectable(
								selected = defaultVisibility == "direct",
								role = Role.RadioButton,
								onClick = { putDefaultVisibility("direct") }
							)
					) {
						RadioButton(
							selected = defaultVisibility == "direct",
							onClick = null,
							modifier = Modifier.padding(start = 10.dp)
						)
						Text(
							stringResource(Res.string.visibility_direct),
							modifier = Modifier.padding(start = 20.dp)
						)
					}
				}
			}
		}
		item {
			val haptics by settings.getBooleanFlow("haptics", true)
				.collectAsStateWithLifecycle(true)

			Card(
				modifier = Modifier.listItemClip(0, 2).padding(bottom = 2.dp),
				shape = ListItemShape(0, 3),
			) {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.haptics)) },
					trailingContent = {
						Switch(
							haptics,
							onCheckedChange = { blockingSettings.putBoolean("haptics", it) }
						)
					},
					modifier = Modifier.clickable {
						blockingSettings.putBoolean("haptics", !haptics)
					},
					colors = ListItemDefaults.colors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
				)
			}
		}
		item {
			val timelineLocked by settings.getBooleanFlow("timeline_locked", false)
				.collectAsStateWithLifecycle(false)

			Card(
				modifier = Modifier.listItemClip(1, 3).padding(bottom = 2.dp),
				shape = ListItemShape(1, 3),
			) {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.lock_timeline)) },
					supportingContent = { Text(stringResource(Res.string.lock_timeline_short_description)) },
					trailingContent = {
						Switch(
							timelineLocked,
							onCheckedChange = { blockingSettings.putBoolean("timeline_locked", it) }
						)
					},
					modifier = Modifier.clickable {
						blockingSettings.putBoolean("timeline_locked", !timelineLocked)
					},
					colors = ListItemDefaults.colors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
				)
			}
		}
		item {
			val timelineLocked by settings.getBooleanFlow("append_re_on_replies", true)
				.collectAsStateWithLifecycle(true)

			Card(
				modifier = Modifier.listItemClip(2, 3).padding(bottom = 10.dp),
				shape = ListItemShape(2, 3),
			) {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.append_re_on_reply_content_warnings)) },
					trailingContent = {
						Switch(
							timelineLocked,
							onCheckedChange = { blockingSettings.putBoolean("append_re_on_replies", it) }
						)
					},
					modifier = Modifier.clickable {
						blockingSettings.putBoolean("append_re_on_replies", !timelineLocked)
					},
					colors = ListItemDefaults.colors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
				)
			}
		}
		item {
			// idk why but it only works to use blockingSettings here. normal settings flow thing always just returns the default 20
			val maxRecentEmojis = blockingSettings.getInt("max_recent_emojis", 20)
			val sliderState = rememberSliderState(
				value = maxRecentEmojis.toFloat(),
				valueRange = 5f..50f,
				steps = 8
			).apply { // wtf is an apply and why do i need to do this Here
				onValueChangeFinished = {
					blockingSettings.putInt("max_recent_emojis", value.roundToInt())
				}
			}

			Card(
				modifier = Modifier.listItemClip(0, 3).padding(bottom = 2.dp),
				shape = ListItemShape(0, 3),
			) {
				ListItem(
					headlineContent = {
						Text(
							translation(
								Res.string.number_of_recent_emojis_to_save,
								mapOf("number" to AnnotatedString(
									sliderState.value.roundToInt().toString()
								))
							)
						)
					},
					supportingContent = {
						Column {
							Slider(state = sliderState)
						}
					},
					colors = ListItemDefaults.colors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
				)
			}
		}
		item {
			val postsPerPage = blockingSettings.getInt("posts_per_page", 30)
			val sliderState = rememberSliderState(
				value = postsPerPage.toFloat(),
				valueRange = 15f..40f,
				steps = 4
			).apply {
				onValueChangeFinished = {
					blockingSettings.putInt("posts_per_page", value.roundToInt())
				}
			}

			Card(
				modifier = Modifier.listItemClip(1, 3).padding(bottom = 2.dp),
				shape = ListItemShape(1, 3),
			) {
				ListItem(
					headlineContent = {
						Text(
							translation(
								Res.string.posts_per_page,
								mapOf("number" to AnnotatedString(
									sliderState.value.roundToInt().toString()
								))
							)
						)
					},
					supportingContent = {
						Column {
							Slider(state = sliderState)
						}
					},
					colors = ListItemDefaults.colors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
				)
			}
		}
		item {
			val notifsPerPage = blockingSettings.getInt("notifs_per_page", 100)
			val sliderState = rememberSliderState(
				value = notifsPerPage.toFloat(),
				valueRange = 15f..100f,
				steps = 16
			).apply {
				onValueChangeFinished = {
					blockingSettings.putInt("notifs_per_page", value.roundToInt())
				}
			}

			Card(
				modifier = Modifier.listItemClip(2, 3), // add padding
				shape = ListItemShape(2, 3),
			) {
				ListItem(
					headlineContent = {
						Text(
							translation(
								Res.string.notifs_per_page,
								mapOf("number" to AnnotatedString(
									sliderState.value.roundToInt().toString()
								))
							)
						)
					},
					supportingContent = {
						Column {
							Slider(state = sliderState)
						}
					},
					colors = ListItemDefaults.colors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
				)
			}
		}
	}
}
