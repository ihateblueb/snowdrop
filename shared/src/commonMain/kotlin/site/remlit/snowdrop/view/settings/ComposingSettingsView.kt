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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.append_re_on_reply_content_warnings
import snowdrop.shared.generated.resources.composing
import snowdrop.shared.generated.resources.default_post_visibility
import snowdrop.shared.generated.resources.icon_keyboard_arrow_down_24px
import snowdrop.shared.generated.resources.icon_keyboard_arrow_up_24px
import snowdrop.shared.generated.resources.visibility_direct
import snowdrop.shared.generated.resources.visibility_followers
import snowdrop.shared.generated.resources.visibility_public
import snowdrop.shared.generated.resources.visibility_unlisted
import snowdrop.shared.generated.resources.warn_when_posting_publicly
import snowdrop.shared.generated.resources.whenever_you_post_publicly_a_confirmation_dialog_will_pop_up
import kotlin.math.roundToInt

@Composable
fun ComposingSettingsView() = ViewSurface {
	val navHandler = LocalNavController.current

	var showVisibilityPicker by remember { mutableStateOf(false) }

	TopAppBar(
		navigationIcon = { NavigationBackButton() },
		title = {
			Text(stringResource(Res.string.composing))
		}
	)

	LazyColumn(
		modifier = Modifier.padding(horizontal = 10.dp)
	) {
		item {
			val defaultVisibility by remember { getDefaultVisibility() }
				.collectAsStateWithLifecycle("public")

			Card(
				modifier = Modifier.listItemClip(0, 2)
					.padding(bottom = 2.dp),
				shape = ListItemShape(0, 2),
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
				Card(
					modifier = Modifier.listItemClip(1, 2).padding(bottom = 2.dp),
					shape = ListItemShape(1, 2),
					colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
				) {
					Column(
						modifier = Modifier.fillMaxWidth().padding(all = 5.dp)
					) {
						Row(
							verticalAlignment = Alignment.CenterVertically,
							modifier = Modifier.clip(RoundedCornerShape(10.dp))
								.fillMaxWidth().height(42.dp)
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
							modifier = Modifier.clip(RoundedCornerShape(10.dp))
								.fillMaxWidth().height(42.dp)
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
							modifier = Modifier.clip(RoundedCornerShape(10.dp))
								.fillMaxWidth().height(42.dp)
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
							modifier = Modifier.clip(RoundedCornerShape(10.dp))
								.fillMaxWidth().height(42.dp)
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
		item {
			val warnWhenPostingPublicly by settings.getBooleanFlow("warn_when_posting_publicly", false)
				.collectAsStateWithLifecycle(false)

			Card(
				modifier = Modifier.listItemClip(2, 5).padding(bottom = 2.dp),
				shape = ListItemShape(2, 5),
			) {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.warn_when_posting_publicly)) },
					supportingContent = { Text(stringResource(Res.string.whenever_you_post_publicly_a_confirmation_dialog_will_pop_up)) },
					trailingContent = {
						Switch(
							warnWhenPostingPublicly,
							onCheckedChange = { blockingSettings.putBoolean("warn_when_posting_publicly", it) }
						)
					},
					modifier = Modifier.clickable {
						blockingSettings.putBoolean("warn_when_posting_publicly", !warnWhenPostingPublicly)
					},
					colors = ListItemDefaults.colors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
				)
			}
		}
		item {
			val appendReOnReplies by settings.getBooleanFlow("append_re_on_replies", true)
				.collectAsStateWithLifecycle(true)

			Card(
				modifier = Modifier.listItemClip(3, 4).padding(bottom = 2.dp),
				shape = ListItemShape(3, 4),
			) {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.append_re_on_reply_content_warnings)) },
					trailingContent = {
						Switch(
							appendReOnReplies,
							onCheckedChange = { blockingSettings.putBoolean("append_re_on_replies", it) }
						)
					},
					modifier = Modifier.clickable {
						blockingSettings.putBoolean("append_re_on_replies", !appendReOnReplies)
					},
					colors = ListItemDefaults.colors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
				)
			}
		}
	}
}
