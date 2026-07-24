package site.remlit.snowdrop.view.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import co.touchlab.kermit.Logger
import com.russhwolf.settings.ExperimentalSettingsApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableColumn
import site.remlit.snowdrop.AboutSettingsRoute
import site.remlit.snowdrop.AppearanceSettingsRoute
import site.remlit.snowdrop.GeneralSettingsRoute
import site.remlit.snowdrop.StartRoute
import site.remlit.snowdrop.WellbeingSettingsRoute
import site.remlit.snowdrop.component.SettingsCard
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.component.navigationBar.NavigationBarIcon
import site.remlit.snowdrop.component.navigationBar.NavigationBarLabel
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.getNavigationBarOrderBlocking
import site.remlit.snowdrop.util.getCurrentAccountId
import site.remlit.snowdrop.util.logoutAccount
import site.remlit.snowdrop.util.mapToNavigationOptions
import site.remlit.snowdrop.util.putNavigationBarOrder
import site.remlit.snowdrop.util.settings
import site.remlit.snowdrop.util.showAccountSwitcher
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.about
import snowdrop.shared.generated.resources.account
import snowdrop.shared.generated.resources.always_show_compose_button
import snowdrop.shared.generated.resources.appearance
import snowdrop.shared.generated.resources.general
import snowdrop.shared.generated.resources.hide_follow_counters
import snowdrop.shared.generated.resources.hide_interaction_counters
import snowdrop.shared.generated.resources.icon_arrow_back_24
import snowdrop.shared.generated.resources.icon_drag_indicator_24px
import snowdrop.shared.generated.resources.icon_favorite_24px
import snowdrop.shared.generated.resources.icon_info_24px
import snowdrop.shared.generated.resources.icon_keyboard_arrow_down_24px
import snowdrop.shared.generated.resources.icon_keyboard_arrow_up_24px
import snowdrop.shared.generated.resources.icon_logout_24px
import snowdrop.shared.generated.resources.icon_palette_24px
import snowdrop.shared.generated.resources.icon_settings_24px
import snowdrop.shared.generated.resources.icon_switch_account_24px
import snowdrop.shared.generated.resources.logout
import snowdrop.shared.generated.resources.navigation_bar_tab_order
import snowdrop.shared.generated.resources.settings
import snowdrop.shared.generated.resources.show_navigation_bar_labels
import snowdrop.shared.generated.resources.switch_account
import snowdrop.shared.generated.resources.use_amoled_dark_theme
import snowdrop.shared.generated.resources.using_on_a_nonamoled_screen_may_cause_contrast_issues
import snowdrop.shared.generated.resources.wellbeing

val dropdownEnterAnimation = expandVertically() + fadeIn()
val dropdownExitAnimation = fadeOut() + shrinkVertically()

@Composable
@OptIn(ExperimentalSettingsApi::class)
fun SettingsView() = ViewSurface {
	val navHandler = LocalNavController.current

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
		// about
		item {
			SettingsCard(
				position = 0, size = 1,
				icon = { color, modifier ->
					Icon(painterResource(Res.drawable.icon_info_24px), null,
						modifier = modifier, tint = color)
				},
				headlineContent = stringResource(Res.string.about),
				onClick = { navHandler.navigate(AboutSettingsRoute) }
			)
		}

		//<editor-fold name="General">
		//general
		item {
			SettingsCard(
				position = 0, size = 3,
				icon = { color, modifier ->
					Icon(painterResource(Res.drawable.icon_settings_24px), null,
						modifier = modifier, tint = color)
				},
				headlineContent = stringResource(Res.string.general),
				onClick = { navHandler.navigate(GeneralSettingsRoute) }
			)
		}
		item {
			SettingsCard(
				position = 1, size = 3,
				icon = { color, modifier ->
					Icon(painterResource(Res.drawable.icon_palette_24px), null,
						modifier = modifier, tint = color)
				},
				headlineContent = stringResource(Res.string.appearance),
				onClick = { navHandler.navigate(AppearanceSettingsRoute) }
			)
		}
		item {
			SettingsCard(
				position = 2, size = 3,
				icon = { color, modifier ->
					Icon(painterResource(Res.drawable.icon_favorite_24px), null,
						modifier = modifier, tint = color)
				},
				headlineContent = stringResource(Res.string.wellbeing),
				onClick = { navHandler.navigate(WellbeingSettingsRoute) }
			)
		}


		item {
			SettingsCard(
				position = 0, size = 2,
				icon = { color, modifier ->
					Icon(painterResource(Res.drawable.icon_switch_account_24px), null,
						modifier = modifier, tint = color)
				},
				headlineContent = stringResource(Res.string.switch_account),
				onClick = { showAccountSwitcher = true }
			)
		}
		item {
			SettingsCard(
				position = 1, size = 2,
				icon = { color, modifier ->
					Icon(painterResource(Res.drawable.icon_logout_24px), null,
						modifier = modifier, tint = color)
				},
				headlineContent = stringResource(Res.string.logout),
				onClick = {
					logoutAccount(getCurrentAccountId())
					navHandler.navigate(StartRoute)
				}
			)
		}
	}
}
