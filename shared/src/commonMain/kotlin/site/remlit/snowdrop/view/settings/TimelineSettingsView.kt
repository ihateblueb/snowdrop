package site.remlit.snowdrop.view.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.component.NavigationBackButton
import site.remlit.snowdrop.component.SettingOrder
import site.remlit.snowdrop.component.SettingsCard
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.component.Visibility
import site.remlit.snowdrop.model.Platform
import site.remlit.snowdrop.util.ListItemShape
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.getDefaultVisibility
import site.remlit.snowdrop.util.getPlatform
import site.remlit.snowdrop.util.listItemClip
import site.remlit.snowdrop.util.putDefaultVisibility
import site.remlit.snowdrop.util.settings
import site.remlit.snowdrop.util.translation
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.append_re_on_reply_content_warnings
import snowdrop.shared.generated.resources.automatically_choose
import snowdrop.shared.generated.resources.default_post_visibility
import snowdrop.shared.generated.resources.disable_attachments_download
import snowdrop.shared.generated.resources.general
import snowdrop.shared.generated.resources.haptics
import snowdrop.shared.generated.resources.icon_keyboard_arrow_down_24px
import snowdrop.shared.generated.resources.icon_keyboard_arrow_up_24px
import snowdrop.shared.generated.resources.image_conversion
import snowdrop.shared.generated.resources.ios_requires_image_conversion
import snowdrop.shared.generated.resources.jpeg_quality
import snowdrop.shared.generated.resources.lock_timeline
import snowdrop.shared.generated.resources.lock_timeline_short_description
import snowdrop.shared.generated.resources.notifs_per_page
import snowdrop.shared.generated.resources.number_of_recent_emojis_to_save
import snowdrop.shared.generated.resources.posts_per_page
import snowdrop.shared.generated.resources.timeline
import snowdrop.shared.generated.resources.visibility_direct
import snowdrop.shared.generated.resources.visibility_followers
import snowdrop.shared.generated.resources.visibility_public
import snowdrop.shared.generated.resources.visibility_unlisted
import snowdrop.shared.generated.resources.warn_when_posting_publicly
import snowdrop.shared.generated.resources.whenever_you_post_publicly_a_confirmation_dialog_will_pop_up
import kotlin.math.roundToInt

@Composable
fun TimelineSettingsView() = ViewSurface {
	val navHandler = LocalNavController.current

	var showVisibilityPicker by remember { mutableStateOf(false) }

	TopAppBar(
		navigationIcon = { NavigationBackButton() },
		title = {
			Text(stringResource(Res.string.timeline))
		}
	)

	LazyColumn(
		modifier = Modifier.padding(horizontal = 10.dp)
	) {
		item {
			val timelineLocked by settings.getBooleanFlow("timeline_locked", false)
				.collectAsStateWithLifecycle(false)

			SettingsCard(
				order = SettingOrder.Start,
				headlineContent = stringResource(Res.string.lock_timeline),
				supportingContent = {
					Text(
						text = stringResource(Res.string.lock_timeline_short_description),
						fontSize = 13.sp
					)
				},
				trailingContent = {
					Switch(
						timelineLocked,
						onCheckedChange = { blockingSettings.putBoolean("timeline_locked", it) }
					)
				},
				onClick = {
					blockingSettings.putBoolean("timeline_locked", !timelineLocked)
				}
			)
		}
		item {
			val disableAttachmentsDownload by settings.getBooleanFlow("disable_attachments_download", false)
				.collectAsStateWithLifecycle(false)

			SettingsCard(
				order = SettingOrder.End,
				headlineContent = stringResource(Res.string.disable_attachments_download),
				trailingContent = {
					Switch(
						disableAttachmentsDownload,
						onCheckedChange = { blockingSettings.putBoolean("disable_attachments_download", it) }
					)
				},
				onClick = {
					blockingSettings.putBoolean("disable_attachments_download", !disableAttachmentsDownload)
				}
			)
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

			SettingsCard(
				order = SettingOrder.Start,
				headlineContent = translation(
					Res.string.posts_per_page,
					mapOf("number" to AnnotatedString(
						sliderState.value.roundToInt().toString()
					))
				).text,
				supportingContent = {
					Column {
						Slider(state = sliderState)
					}
				},
			)
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

			SettingsCard(
				order = SettingOrder.End,
				headlineContent = translation(
					Res.string.notifs_per_page,
					mapOf("number" to AnnotatedString(
						sliderState.value.roundToInt().toString()
					))
				).text,
				supportingContent = {
					Column {
						Slider(state = sliderState)
					}
				},
			)
		}
	}
}
