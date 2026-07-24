package site.remlit.snowdrop.view.settings

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.component.Visibility
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.getDefaultVisibility
import site.remlit.snowdrop.util.putDefaultVisibility
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.default_post_visibility
import snowdrop.shared.generated.resources.general
import snowdrop.shared.generated.resources.icon_arrow_back_24
import snowdrop.shared.generated.resources.icon_keyboard_arrow_down_24px
import snowdrop.shared.generated.resources.icon_keyboard_arrow_up_24px
import snowdrop.shared.generated.resources.visibility_direct
import snowdrop.shared.generated.resources.visibility_followers
import snowdrop.shared.generated.resources.visibility_public
import snowdrop.shared.generated.resources.visibility_unlisted

@Composable
fun GeneralSettingsView() = ViewSurface {
	val navHandler = LocalNavController.current

	TopAppBar(
		navigationIcon = {
			IconButton(onClick = { navHandler.popBackStack() }) {
				Icon(painterResource(Res.drawable.icon_arrow_back_24), null)
			}
		},
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
	}
}
