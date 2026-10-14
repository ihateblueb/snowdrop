@file:Suppress("DEPRECATION")

package site.remlit.snowdrop.view.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.russhwolf.settings.ExperimentalSettingsApi
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.component.NavigationBackButton
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.util.ListItemShape
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.listItemClip
import site.remlit.snowdrop.util.settings
import site.remlit.snowdrop.util.translation
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.general
import snowdrop.shared.generated.resources.haptics
import snowdrop.shared.generated.resources.number_of_recent_emojis_to_save
import kotlin.math.roundToInt

@OptIn(ExperimentalSettingsApi::class)
@Composable
fun GeneralSettingsView() = ViewSurface {
	val navHandler = LocalNavController.current

	var showVisibilityPicker by remember { mutableStateOf(false) }

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
			val haptics by settings.getBooleanFlow("haptics", true)
				.collectAsStateWithLifecycle(true)

			Card(
				modifier = Modifier.listItemClip(0, 2).padding(bottom = 2.dp),
				shape = ListItemShape(0, 2),
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
				modifier = Modifier.listItemClip(1, 2).padding(bottom = 2.dp),
				shape = ListItemShape(1, 2),
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
	}
}
