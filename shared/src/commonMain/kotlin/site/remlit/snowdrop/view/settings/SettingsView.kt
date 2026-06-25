package site.remlit.snowdrop.view.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.russhwolf.settings.ExperimentalSettingsApi
import org.jetbrains.compose.resources.painterResource
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.component.Visibility
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.settings
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_arrow_back_24
import snowdrop.shared.generated.resources.icon_keyboard_arrow_down_24px
import snowdrop.shared.generated.resources.icon_keyboard_arrow_up_24px

@Composable
@OptIn(ExperimentalSettingsApi::class)
fun SettingsView() = ViewSurface {
	val navHandler = LocalNavController.current

	@Composable
	fun Divider() {
		HorizontalDivider(
			thickness = 1.dp,
			color = MaterialTheme.colorScheme.surfaceContainer,
			modifier = Modifier.padding(horizontal = 10.dp)
		)
	}

	TopAppBar(
		navigationIcon = {
			IconButton(onClick = { navHandler.popBackStack() }) {
				Icon(painterResource(Res.drawable.icon_arrow_back_24), null)
			}
		},
		title = {
			Text("Settings")
		}
	)

	LazyColumn(
		modifier = Modifier.padding(horizontal = 10.dp)
	) {
		item {
			Text(
				"General",
				style = MaterialTheme.typography.labelLarge,
				modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
			)
		}
		item {
			val defaultVisibility by settings.getStringFlow("default_visibility", "public")
				.collectAsStateWithLifecycle("public")

			var showVisibilityPicker by remember { mutableStateOf(false) }

			Card {
				ListItem(
					headlineContent = { Text("Default post visibility") },
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
					}
				)
			}
			// todo: replace this
			AnimatedVisibility(
				visible = showVisibilityPicker,
				enter = slideInVertically() + fadeIn(),
				exit = fadeOut() + slideOutVertically(),
			) {
				Column(
					modifier = Modifier.padding(horizontal = 10.dp)
				) {
					Row(
						verticalAlignment = Alignment.CenterVertically
					) {
						RadioButton(
							selected = defaultVisibility == "public",
							onClick = { blockingSettings.putString("default_visibility", "public") }
						)
						Text(
							"Public",
							modifier = Modifier.padding(start = 10.dp)
						)
					}
					Row(
						verticalAlignment = Alignment.CenterVertically
					) {
						RadioButton(
							selected = defaultVisibility == "unlisted",
							onClick = { blockingSettings.putString("default_visibility", "unlisted") }
						)
						Text(
							"Unlisted",
							modifier = Modifier.padding(start = 10.dp)
						)
					}
					Row(
						verticalAlignment = Alignment.CenterVertically
					) {
						RadioButton(
							selected = defaultVisibility == "private",
							onClick = { blockingSettings.putString("default_visibility", "private") }
						)
						Text(
							"Followers",
							modifier = Modifier.padding(start = 10.dp)
						)
					}
					Row(
						verticalAlignment = Alignment.CenterVertically
					) {
						RadioButton(
							selected = defaultVisibility == "direct",
							onClick = { blockingSettings.putString("default_visibility", "direct") }
						)
						Text(
							"Direct",
							modifier = Modifier.padding(start = 10.dp)
						)
					}
				}
			}
		}
		/* item { Divider() } */

		item {
			Text(
				"Wellness",
				style = MaterialTheme.typography.labelLarge,
				modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
			)
		}
		item {
			val hideInteractionCounters by settings.getBooleanFlow("hide_interaction_counters", false)
				.collectAsStateWithLifecycle(false)

			Card {
				ListItem(
					headlineContent = { Text("Hide interaction counters on posts") },
					trailingContent = {
						Switch(
							hideInteractionCounters,
							onCheckedChange = { blockingSettings.putBoolean("hide_interaction_counters", it) }
						)
					}
				)
			}
		}
		item {
			val hideFollowCounters by settings.getBooleanFlow("hide_follow_counters", false)
				.collectAsStateWithLifecycle(false)

			Card {
				ListItem(
					headlineContent = { Text("Hide follow counters") },
					trailingContent = {
						Switch(
							hideFollowCounters,
							onCheckedChange = { blockingSettings.putBoolean("hide_follow_counters", it) }
						)
					}
				)
			}
		}
	}
}
