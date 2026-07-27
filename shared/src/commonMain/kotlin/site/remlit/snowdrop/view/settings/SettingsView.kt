package site.remlit.snowdrop.view.settings

import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.russhwolf.settings.ExperimentalSettingsApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.AboutSettingsRoute
import site.remlit.snowdrop.AppearanceSettingsRoute
import site.remlit.snowdrop.GeneralSettingsRoute
import site.remlit.snowdrop.StartRoute
import site.remlit.snowdrop.WellbeingSettingsRoute
import site.remlit.snowdrop.component.SettingsCard
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.getCurrentAccountId
import site.remlit.snowdrop.util.logoutAccount
import site.remlit.snowdrop.util.showAccountSwitcher
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.about
import snowdrop.shared.generated.resources.appearance
import snowdrop.shared.generated.resources.general
import snowdrop.shared.generated.resources.icon_arrow_back_24
import snowdrop.shared.generated.resources.icon_favorite_24px
import snowdrop.shared.generated.resources.icon_info_24px
import snowdrop.shared.generated.resources.icon_logout_24px
import snowdrop.shared.generated.resources.icon_palette_24px
import snowdrop.shared.generated.resources.icon_settings_24px
import snowdrop.shared.generated.resources.icon_switch_account_24px
import snowdrop.shared.generated.resources.logout
import snowdrop.shared.generated.resources.settings
import snowdrop.shared.generated.resources.switch_account
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
