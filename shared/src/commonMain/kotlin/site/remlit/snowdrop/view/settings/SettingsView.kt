package site.remlit.snowdrop.view.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import com.russhwolf.settings.ExperimentalSettingsApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableColumn
import site.remlit.snowdrop.AboutInstanceRoute
import site.remlit.snowdrop.AboutSnowdropRoute
import site.remlit.snowdrop.DebugRoute
import site.remlit.snowdrop.StartRoute
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.component.Visibility
import site.remlit.snowdrop.component.navigationBar.NavigationBarIcon
import site.remlit.snowdrop.component.navigationBar.NavigationBarLabel
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.getNavigationBarOrderBlocking
import site.remlit.snowdrop.util.getCurrentAccountId
import site.remlit.snowdrop.util.getDefaultVisibility
import site.remlit.snowdrop.util.logoutAccount
import site.remlit.snowdrop.util.mapToNavigationOptions
import site.remlit.snowdrop.util.putNavigationBarOrder
import site.remlit.snowdrop.util.putDefaultVisibility
import site.remlit.snowdrop.util.settings
import site.remlit.snowdrop.util.showAccountSwitcher
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.about_instance
import snowdrop.shared.generated.resources.about_snowdrop
import snowdrop.shared.generated.resources.account
import snowdrop.shared.generated.resources.always_show_compose_button
import snowdrop.shared.generated.resources.appearance
import snowdrop.shared.generated.resources.debug
import snowdrop.shared.generated.resources.default_post_visibility
import snowdrop.shared.generated.resources.general
import snowdrop.shared.generated.resources.hide_follow_counters
import snowdrop.shared.generated.resources.hide_interaction_counters
import snowdrop.shared.generated.resources.icon_arrow_back_24
import snowdrop.shared.generated.resources.icon_bug_report_24px
import snowdrop.shared.generated.resources.icon_chevron_right_24px
import snowdrop.shared.generated.resources.icon_drag_indicator_24px
import snowdrop.shared.generated.resources.icon_info_24px
import snowdrop.shared.generated.resources.icon_keyboard_arrow_down_24px
import snowdrop.shared.generated.resources.icon_keyboard_arrow_up_24px
import snowdrop.shared.generated.resources.icon_logout_24px
import snowdrop.shared.generated.resources.icon_switch_account_24px
import snowdrop.shared.generated.resources.logout
import snowdrop.shared.generated.resources.navigation_bar_tab_order
import snowdrop.shared.generated.resources.settings
import snowdrop.shared.generated.resources.show_navigation_bar_labels
import snowdrop.shared.generated.resources.switch_account
import snowdrop.shared.generated.resources.use_amoled_dark_theme
import snowdrop.shared.generated.resources.using_on_a_nonamoled_screen_may_cause_contrast_issues
import snowdrop.shared.generated.resources.visibility_direct
import snowdrop.shared.generated.resources.visibility_followers
import snowdrop.shared.generated.resources.visibility_public
import snowdrop.shared.generated.resources.visibility_unlisted
import snowdrop.shared.generated.resources.wellbeing

@Composable
@OptIn(ExperimentalSettingsApi::class)
fun SettingsView() = ViewSurface {
	val navHandler = LocalNavController.current

	val dropdownEnterAnimation = expandVertically() + fadeIn()
	val dropdownExitAnimation = fadeOut() + shrinkVertically()

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
			Text(stringResource(Res.string.settings))
		}
	)

	LazyColumn(
		modifier = Modifier.padding(horizontal = 10.dp)
	) {
		// about instance
		item {
			Card {
				ListItem(
					leadingContent = {
						Icon(painterResource(Res.drawable.icon_info_24px), null)
					},
					headlineContent = { Text(stringResource(Res.string.about_instance)) },
					modifier = Modifier.clickable {
						navHandler.navigate(AboutInstanceRoute)
					}
				)
			}
		}
		// about snowdrop
		item {
			Card {
				ListItem(
					leadingContent = {
						Icon(painterResource(Res.drawable.icon_info_24px), null)
					},
					headlineContent = { Text(stringResource(Res.string.about_snowdrop)) },
					modifier = Modifier.clickable {
						navHandler.navigate(AboutSnowdropRoute)
					}
				)
			}
		}

		//<editor-fold name="General">
		//general
		item {
			Text(
				stringResource(Res.string.general),
				style = MaterialTheme.typography.labelLarge,
				modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 10.dp)
			)
		}
		//default post vis
		item {
			val defaultVisibility by remember { getDefaultVisibility() }
				.collectAsStateWithLifecycle("public")

			var showVisibilityPicker by remember { mutableStateOf(false) }

			Card {
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
					}
				)
			}
			AnimatedVisibility(
				visible = showVisibilityPicker,
				enter = dropdownEnterAnimation,
				exit = dropdownExitAnimation
			) {
				Column(
					modifier = Modifier.padding(horizontal = 10.dp)
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
		//</editor-fold>

		//<editor-fold name="Appearance">
		item {
			Text(
				stringResource(Res.string.appearance),
				style = MaterialTheme.typography.labelLarge,
				modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 10.dp)
			)
		}
		item {
			val amoledBlack by settings.getBooleanFlow("amoled_black", false)
				.collectAsStateWithLifecycle(false)

			Card {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.use_amoled_dark_theme)) },
					supportingContent = { Text(stringResource(Res.string.using_on_a_nonamoled_screen_may_cause_contrast_issues)) },
					trailingContent = {
						Switch(
							amoledBlack,
							onCheckedChange = { blockingSettings.putBoolean("amoled_black", it) }
						)
					}
				)
			}
		}
		item {
			val alwaysShowComposeButton by settings.getBooleanFlow("always_show_compose_button", false)
				.collectAsStateWithLifecycle(false)

			Card {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.always_show_compose_button)) },
					trailingContent = {
						Switch(
							alwaysShowComposeButton,
							onCheckedChange = { blockingSettings.putBoolean("always_show_compose_button", it) }
						)
					}
				)
			}
		}
		item {
			val showNavigationBarLabels by settings.getBooleanFlow("show_navigation_bar_labels", true)
				.collectAsStateWithLifecycle(true)

			Card {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.show_navigation_bar_labels)) },
					trailingContent = {
						Switch(
							showNavigationBarLabels,
							onCheckedChange = { blockingSettings.putBoolean("show_navigation_bar_labels", it) }
						)
					}
				)
			}
		}
		item {
			var tabOrder by remember { mutableStateOf(getNavigationBarOrderBlocking().mapToNavigationOptions()) }

			LaunchedEffect(tabOrder) {
				Logger.d { "(launched effect) taborder $tabOrder" }
				putNavigationBarOrder(tabOrder.joinToString(separator = " "))
			}

			var showBottomBarTabOrder by remember { mutableStateOf(false) }

			Card {
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
					}
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
					modifier = Modifier.padding(horizontal = 10.dp)
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
		//</editor-fold>

		//<editor-fold name="Wellbeing">
		item {
			Text(
				stringResource(Res.string.wellbeing),
				style = MaterialTheme.typography.labelLarge,
				modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 10.dp)
			)
		}
		item {
			val hideInteractionCounters by settings.getBooleanFlow("hide_interaction_counters", false)
				.collectAsStateWithLifecycle(false)

			Card {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.hide_interaction_counters)) },
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
					headlineContent = { Text(stringResource(Res.string.hide_follow_counters)) },
					trailingContent = {
						Switch(
							hideFollowCounters,
							onCheckedChange = { blockingSettings.putBoolean("hide_follow_counters", it) }
						)
					}
				)
			}
		}
		//</editor-fold>

		//<editor-fold name="Account">
		item {
			Text(
				stringResource(Res.string.account),
				style = MaterialTheme.typography.labelLarge,
				modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 10.dp)
			)
		}
		item {
			Card {
				ListItem(
					leadingContent = {
						Icon(painterResource(Res.drawable.icon_switch_account_24px), null)
					},
					headlineContent = { Text(stringResource(Res.string.switch_account)) },
					modifier = Modifier.clickable {
						showAccountSwitcher = true
					}
				)
			}
		}
		item {
			Card {
				ListItem(
					leadingContent = {
						Icon(
							painterResource(Res.drawable.icon_logout_24px), null,
							tint = MaterialTheme.colorScheme.error
						)
					},
					headlineContent = { Text(stringResource(Res.string.logout), color = MaterialTheme.colorScheme.error) },
					modifier = Modifier.clickable {
						val id = getCurrentAccountId()
						logoutAccount(id)
						navHandler.navigate(StartRoute)
					}
				)
			}
		}
		//</editor-fold>
	}
}
