@file:Suppress("DEPRECATION")

package site.remlit.snowdrop.view.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.russhwolf.settings.ExperimentalSettingsApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableColumn
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.component.navigationBar.NavigationBarIcon
import site.remlit.snowdrop.component.navigationBar.NavigationBarLabel
import site.remlit.snowdrop.util.ListItemShape
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.getNavigationBarOrderBlocking
import site.remlit.snowdrop.util.listItemClip
import site.remlit.snowdrop.util.log.debug
import site.remlit.snowdrop.util.mapToNavigationOptions
import site.remlit.snowdrop.util.putNavigationBarOrder
import site.remlit.snowdrop.util.settings
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.always_show_compose_button
import snowdrop.shared.generated.resources.appearance
import snowdrop.shared.generated.resources.icon_arrow_back_24
import snowdrop.shared.generated.resources.icon_drag_indicator_24px
import snowdrop.shared.generated.resources.icon_keyboard_arrow_down_24px
import snowdrop.shared.generated.resources.icon_keyboard_arrow_up_24px
import snowdrop.shared.generated.resources.navigation_bar_tab_order
import snowdrop.shared.generated.resources.show_navigation_bar_labels
import snowdrop.shared.generated.resources.show_send_post_at_bottom_of_composer
import snowdrop.shared.generated.resources.use_amoled_dark_theme
import snowdrop.shared.generated.resources.using_on_a_nonamoled_screen_may_cause_contrast_issues

@Composable
@OptIn(ExperimentalSettingsApi::class)
fun AppearanceSettingsView() = ViewSurface {
	val navHandler = LocalNavController.current

	TopAppBar(
		navigationIcon = {
			IconButton(onClick = { navHandler.popBackStack() }) {
				Icon(painterResource(Res.drawable.icon_arrow_back_24), null)
			}
		},
		title = {
			Text(stringResource(Res.string.appearance))
		}
	)

	LazyColumn(
		modifier = Modifier.padding(horizontal = 10.dp)
	) {
		item {
			val amoledBlack by settings.getBooleanFlow("amoled_black", false)
				.collectAsStateWithLifecycle(false)

			Card(
				modifier = Modifier.listItemClip(0, 4).padding(bottom = 2.dp),
				shape = ListItemShape(0, 4),
			) {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.use_amoled_dark_theme)) },
					supportingContent = { Text(stringResource(Res.string.using_on_a_nonamoled_screen_may_cause_contrast_issues)) },
					trailingContent = {
						Switch(
							amoledBlack,
							onCheckedChange = { blockingSettings.putBoolean("amoled_black", it) }
						)
					},
					modifier = Modifier.clickable {
						blockingSettings.putBoolean("amoled_black", !amoledBlack)
					},
					colors = ListItemDefaults.colors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
				)
			}
		}
		item {
			val alwaysShowComposeButton by settings.getBooleanFlow("always_show_compose_button", false)
				.collectAsStateWithLifecycle(false)

			Card(
				modifier = Modifier.listItemClip(1, 4).padding(bottom = 2.dp),
				shape = ListItemShape(1, 4),
			) {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.always_show_compose_button)) },
					trailingContent = {
						Switch(
							alwaysShowComposeButton,
							onCheckedChange = { blockingSettings.putBoolean("always_show_compose_button", it) }
						)
					},
					modifier = Modifier.clickable {
						blockingSettings.putBoolean("always_show_compose_button", !alwaysShowComposeButton)
					},
					colors = ListItemDefaults.colors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
				)
			}
		}
		item {
			val showNavigationBarLabels by settings.getBooleanFlow("show_navigation_bar_labels", true)
				.collectAsStateWithLifecycle(true)

			Card(
				modifier = Modifier.listItemClip(2, 4).padding(bottom = 2.dp),
				shape = ListItemShape(2, 4),
			) {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.show_navigation_bar_labels)) },
					trailingContent = {
						Switch(
							showNavigationBarLabels,
							onCheckedChange = { blockingSettings.putBoolean("show_navigation_bar_labels", it) }
						)
					},
					modifier = Modifier.clickable {
						blockingSettings.putBoolean("show_navigation_bar_labels", !showNavigationBarLabels)
					},
					colors = ListItemDefaults.colors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
				)
			}
		}
		item {
			val swapPostButtonAndCharLimit by settings.getBooleanFlow("swap_post_button_and_char_limit", false)
				.collectAsStateWithLifecycle(false)

			Card(
				modifier = Modifier.listItemClip(3, 4).padding(bottom = 10.dp),
				shape = ListItemShape(3, 4),
			) {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.show_send_post_at_bottom_of_composer)) },
					trailingContent = {
						Switch(
							swapPostButtonAndCharLimit,
							onCheckedChange = { blockingSettings.putBoolean("swap_post_button_and_char_limit", it) }
						)
					},
					modifier = Modifier.clickable {
						blockingSettings.putBoolean("swap_post_button_and_char_limit", !swapPostButtonAndCharLimit)
					},
					colors = ListItemDefaults.colors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
				)
			}
		}
		item {
			var tabOrder by remember { mutableStateOf(getNavigationBarOrderBlocking().mapToNavigationOptions()) }

			LaunchedEffect(tabOrder) {
				debug { "(AppearanceSettingsView) launched effect taborder $tabOrder" }
				putNavigationBarOrder(tabOrder.joinToString(separator = " "))
			}

			var showBottomBarTabOrder by remember { mutableStateOf(false) }

			Card(
				modifier = Modifier.listItemClip(0, 1), // add padding if we do more options
				shape = ListItemShape(0, 1),
			) {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.navigation_bar_tab_order)) },
					trailingContent = {
						Row(
							horizontalArrangement = Arrangement.spacedBy(10.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							if (showBottomBarTabOrder) Icon(painterResource(Res.drawable.icon_keyboard_arrow_up_24px), null)
							else Icon(painterResource(Res.drawable.icon_keyboard_arrow_down_24px), null)
						}
					},
					modifier = Modifier.clickable {
						showBottomBarTabOrder = !showBottomBarTabOrder
					},
					colors = ListItemDefaults.colors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
				)
			}
			AnimatedVisibility(
				visible = showBottomBarTabOrder,
				enter = dropdownEnterAnimation,
				exit = dropdownExitAnimation
			) {
				ReorderableColumn(
					list = tabOrder,
					onSettle = { from, to ->
						tabOrder = tabOrder.toMutableList().apply {
							add(to, removeAt(from))
						}
					},
					modifier = Modifier.padding(horizontal = 10.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh)
				) { _, item, _ ->
					key(item) {
						ReorderableItem {
							Row(
								Modifier.fillMaxWidth().padding(vertical = 5.dp, horizontal = 16.dp),
								horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
								verticalAlignment = Alignment.CenterVertically
							) {
								NavigationBarIcon(item)
								Text(NavigationBarLabel(item))

								Row(
									modifier = Modifier.weight(1f),
									horizontalArrangement = Arrangement.End
								) {
									IconButton(
										modifier = Modifier.draggableHandle(
											onDragStarted = {},
											onDragStopped = {},
										),
										onClick = {},
									) {
										Icon(painterResource(Res.drawable.icon_drag_indicator_24px), contentDescription = "Reorder")
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
