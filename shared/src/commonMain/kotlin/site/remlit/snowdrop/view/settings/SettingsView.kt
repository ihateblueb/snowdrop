package site.remlit.snowdrop.view.settings

import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.AboutSettingsRoute
import site.remlit.snowdrop.AppearanceSettingsRoute
import site.remlit.snowdrop.GeneralSettingsRoute
import site.remlit.snowdrop.LoginRoute
import site.remlit.snowdrop.WellbeingSettingsRoute
import site.remlit.snowdrop.component.Avatar
import site.remlit.snowdrop.component.NavigationBackButton
import site.remlit.snowdrop.component.SettingOrder
import site.remlit.snowdrop.component.SettingsCard
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.component.avatarSize
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.atRoute
import site.remlit.snowdrop.util.getCurrentAccountId
import site.remlit.snowdrop.util.getCurrentAccountObject
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow
import site.remlit.snowdrop.util.logoutAccount
import site.remlit.snowdrop.util.showAccountSwitcher
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.about
import snowdrop.shared.generated.resources.appearance
import snowdrop.shared.generated.resources.composing
import snowdrop.shared.generated.resources.general
import snowdrop.shared.generated.resources.icon_edit_24px
import snowdrop.shared.generated.resources.icon_favorite_24px
import snowdrop.shared.generated.resources.icon_info_24px
import snowdrop.shared.generated.resources.icon_list_24px
import snowdrop.shared.generated.resources.icon_logout_24px
import snowdrop.shared.generated.resources.icon_palette_24px
import snowdrop.shared.generated.resources.icon_settings_24px
import snowdrop.shared.generated.resources.icon_switch_account_24px
import snowdrop.shared.generated.resources.logout
import snowdrop.shared.generated.resources.settings
import snowdrop.shared.generated.resources.switch_account
import snowdrop.shared.generated.resources.timeline
import snowdrop.shared.generated.resources.wellbeing

val dropdownEnterAnimation = expandVertically() + fadeIn()
val dropdownExitAnimation = fadeOut() + shrinkVertically()

@Composable
fun SettingsView() = ViewSurface {
	val navHandler = LocalNavController.current

	TopAppBar(
		navigationIcon = { NavigationBackButton() },
		title = {
			Text(stringResource(Res.string.settings))
		}
	)

	LazyColumn(
		modifier = Modifier.padding(horizontal = 10.dp)
	) {
		item {
			val currentUser by remember { getCurrentAccountObjectFlow() }
				.collectAsStateWithLifecycle(getCurrentAccountObject())

			if (currentUser != null)
				Box(
					modifier = Modifier.padding(bottom = 20.dp)
				) {
					Card(
						modifier = Modifier.fillMaxWidth()
							.clip(RoundedCornerShape(20.dp)),
						colors = CardDefaults.cardColors(
							containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
						)
					) {
						Row(
							modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 5.dp),
							horizontalArrangement = Arrangement.spacedBy(10.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							Avatar(account = currentUser!!)

							Column {
								Text(
									currentUser!!.displayName(),
									color = MaterialTheme.colorScheme.onSurface,
									fontWeight = FontWeight.Medium,
									fontSize = 16.sp,
									maxLines = 1
								)
								Text(
									"@${currentUser!!.acct}",
									fontSize = 14.sp,
									maxLines = 1
								)
							}
						}

						FlowRow(
							modifier = Modifier.padding(bottom = 10.dp, start = avatarSize.dp + 20.dp),
							horizontalArrangement = Arrangement.spacedBy(5.dp)
						) {
							OutlinedButton(onClick = { showAccountSwitcher = true }) {
								Icon(painterResource(Res.drawable.icon_switch_account_24px), null)
								Spacer(Modifier.size(ButtonDefaults.IconSpacing))
								Text(stringResource(Res.string.switch_account))
							}
							OutlinedButton(onClick = {
								logoutAccount(getCurrentAccountId())
								navHandler.navigate(LoginRoute) {
									popUpTo(navHandler.graph.id) { inclusive = true }
								}
							}) {
								Icon(painterResource(Res.drawable.icon_logout_24px), null)
								Spacer(Modifier.size(ButtonDefaults.IconSpacing))
								Text(stringResource(Res.string.logout))
							}
						}
					}
				}
		}
	}

	LazyColumn(
		modifier = Modifier.padding(horizontal = 10.dp)
	) {
		//<editor-fold name="General">
		item {
			SettingsCard(
				order = SettingOrder.Start,
				icon = { color, modifier ->
					Icon(painterResource(Res.drawable.icon_palette_24px), null,
						modifier = modifier, tint = color)
				},
				headlineContent = stringResource(Res.string.appearance),
				onClick = {
					if (!atRoute<AppearanceSettingsRoute>(navHandler.currentDestination))
						navHandler.navigate(AppearanceSettingsRoute)
				}
			)
		}
		item {
			SettingsCard(
				order = SettingOrder.Middle,
				icon = { color, modifier ->
					Icon(painterResource(Res.drawable.icon_list_24px), null,
						modifier = modifier, tint = color)
				},
				headlineContent = stringResource(Res.string.timeline),
				onClick = {
					if (!atRoute<GeneralSettingsRoute>(navHandler.currentDestination))
						navHandler.navigate(GeneralSettingsRoute)
				}
			)
		}
		item {
			SettingsCard(
				order = SettingOrder.Middle,
				icon = { color, modifier ->
					Icon(painterResource(Res.drawable.icon_edit_24px), null,
						modifier = modifier, tint = color)
				},
				headlineContent = stringResource(Res.string.composing),
				onClick = {
					if (!atRoute<GeneralSettingsRoute>(navHandler.currentDestination))
						navHandler.navigate(GeneralSettingsRoute)
				}
			)
		}
		item {
			SettingsCard(
				order = SettingOrder.End,
				icon = { color, modifier ->
					Icon(painterResource(Res.drawable.icon_favorite_24px), null,
						modifier = modifier, tint = color)
				},
				headlineContent = stringResource(Res.string.wellbeing),
				onClick = {
					if (!atRoute<WellbeingSettingsRoute>(navHandler.currentDestination))
						navHandler.navigate(WellbeingSettingsRoute)
				}
			)
		}


		// about
		item {
			SettingsCard(
				order = SettingOrder.Single,
				icon = { color, modifier ->
					Icon(painterResource(Res.drawable.icon_info_24px), null,
						modifier = modifier, tint = color)
				},
				headlineContent = stringResource(Res.string.about),
				onClick = {
					if (!atRoute<AboutSettingsRoute>(navHandler.currentDestination))
						navHandler.navigate(AboutSettingsRoute)
				}
			)
		}
	}
}
