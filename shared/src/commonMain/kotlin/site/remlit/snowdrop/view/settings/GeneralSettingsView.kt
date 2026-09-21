@file:Suppress("DEPRECATION")

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
import com.russhwolf.settings.ExperimentalSettingsApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.component.NavigationBackButton
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
import snowdrop.shared.generated.resources.visibility_direct
import snowdrop.shared.generated.resources.visibility_followers
import snowdrop.shared.generated.resources.visibility_public
import snowdrop.shared.generated.resources.visibility_unlisted
import snowdrop.shared.generated.resources.warn_when_posting_publicly
import snowdrop.shared.generated.resources.whenever_you_post_publicly_a_confirmation_dialog_will_pop_up
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
		if (getPlatform() == Platform.IOS)
			item {
				val iosImageConversionChoice by settings.getStringFlow("ios_image_conversion_choice", "auto")
					.collectAsStateWithLifecycle("auto")
				val iosJpegQuality = blockingSettings.getInt("ios_jpeg_quality", 85)

				val sliderState = rememberSliderState(
					value = iosJpegQuality.toFloat(),
					valueRange = 0f..100f,
					steps = 100
				).apply {
					onValueChangeFinished = {
						blockingSettings.putInt("ios_jpeg_quality", value.roundToInt())
					}
				}

				var showConversionOptions by remember { mutableStateOf(false) }

				Card(
					modifier = Modifier.listItemClip(1, if (!showConversionOptions) 2 else 3)
						.padding(bottom = if (!showConversionOptions) 10.dp else 2.dp),
					shape = ListItemShape(if (showVisibilityPicker) 0 else 1, if (showVisibilityPicker) 1 else if (!showConversionOptions) 2 else 3),
				) {
					ListItem(
						headlineContent = { Text(stringResource(Res.string.image_conversion)) },
						trailingContent = {
							Row(
								horizontalArrangement = Arrangement.spacedBy(10.dp),
								verticalAlignment = Alignment.CenterVertically
							) {
								//Visibility(defaultVisibility, true)

								if (showConversionOptions) Icon(painterResource(Res.drawable.icon_keyboard_arrow_up_24px), null)
								else Icon(painterResource(Res.drawable.icon_keyboard_arrow_down_24px), null)
							}
						},
						modifier = Modifier.clickable {
							showConversionOptions = !showConversionOptions
						},
						colors = ListItemDefaults.colors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
					)
				}

				AnimatedVisibility(
					visible = showConversionOptions,
					enter = dropdownEnterAnimation,
					exit = dropdownExitAnimation
				) {
					Card(
						modifier = Modifier.listItemClip(1, 2).padding(bottom = 10.dp),
						shape = ListItemShape(1, 2),
						colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
					) {
						Column(
							modifier = Modifier.fillMaxWidth().padding(all = 5.dp)
						) {
							Row(
								verticalAlignment = Alignment.CenterVertically,
								modifier = Modifier.padding(start = 10.dp, top = 5.dp, end = 10.dp)
							) {
								Text(stringResource(Res.string.ios_requires_image_conversion), fontSize = 14.sp)
							}
							Row(
								verticalAlignment = Alignment.CenterVertically,
								modifier = Modifier.clip(RoundedCornerShape(10.dp))
									.fillMaxWidth().height(42.dp)
									.selectable(
										selected = iosImageConversionChoice == "auto",
										role = Role.RadioButton,
										onClick = { blockingSettings.putString("ios_image_conversion_choice", "auto") }
									)
							) {
								RadioButton(
									selected = iosImageConversionChoice == "auto",
									onClick = null,
									modifier = Modifier.padding(start = 10.dp)
								)
								Text(
									stringResource(Res.string.automatically_choose),
									modifier = Modifier.padding(start = 20.dp)
								)
							}
							Row(
								verticalAlignment = Alignment.CenterVertically,
								modifier = Modifier.clip(RoundedCornerShape(10.dp))
									.fillMaxWidth().height(42.dp)
									.selectable(
										selected = iosImageConversionChoice == "heif",
										role = Role.RadioButton,
										onClick = { blockingSettings.putString("ios_image_conversion_choice", "heif") }
									)
							) {
								RadioButton(
									selected = iosImageConversionChoice == "heif",
									onClick = null,
									modifier = Modifier.padding(start = 10.dp)
								)
								Text(
									"HEIF",
									modifier = Modifier.padding(start = 20.dp)
								)
							}
							Row(
								verticalAlignment = Alignment.CenterVertically,
								modifier = Modifier.clip(RoundedCornerShape(10.dp))
									.fillMaxWidth().height(42.dp)
									.selectable(
										selected = iosImageConversionChoice == "png",
										role = Role.RadioButton,
										onClick = { blockingSettings.putString("ios_image_conversion_choice", "png") }
									)
							) {
								RadioButton(
									selected = iosImageConversionChoice == "png",
									onClick = null,
									modifier = Modifier.padding(start = 10.dp)
								)
								Text(
									"PNG",
									modifier = Modifier.padding(start = 20.dp)
								)
							}
							Row(
								verticalAlignment = Alignment.CenterVertically,
								modifier = Modifier.clip(RoundedCornerShape(10.dp))
									.fillMaxWidth().height(42.dp)
									.selectable(
										selected = iosImageConversionChoice == "jpeg",
										role = Role.RadioButton,
										onClick = { blockingSettings.putString("ios_image_conversion_choice", "jpeg") }
									)
							) {
								RadioButton(
									selected = iosImageConversionChoice == "jpeg",
									onClick = null,
									modifier = Modifier.padding(start = 10.dp)
								)
								Text(
									"JPEG",
									modifier = Modifier.padding(start = 20.dp)
								)
							}
							Spacer(Modifier.size(5.dp))

							Column(
								modifier = Modifier.fillMaxWidth().padding(all = 10.dp)
							){
								Row {
									Text(
										translation(
											Res.string.jpeg_quality,
											mapOf("number" to AnnotatedString(
												sliderState.value.roundToInt().toString()
											))
										),
										fontSize = 14.sp
									)
								}
								Row {
									Column {
										Slider(
											state = sliderState,
											enabled = iosImageConversionChoice == "jpeg" || iosImageConversionChoice == "auto",
											track = {
												SliderDefaults.Track(sliderState = sliderState, drawTick = { _, _ -> })
											}
										)
									}
								}
							}
						}
					}
				}
			}
		item {
			val haptics by settings.getBooleanFlow("haptics", true)
				.collectAsStateWithLifecycle(true)

			Card(
				modifier = Modifier.listItemClip(0, 6).padding(bottom = 2.dp),
				shape = ListItemShape(0, 6),
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
			val timelineLocked by settings.getBooleanFlow("timeline_locked", false)
				.collectAsStateWithLifecycle(false)

			Card(
				modifier = Modifier.listItemClip(1, 6).padding(bottom = 2.dp),
				shape = ListItemShape(1, 6),
			) {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.lock_timeline)) },
					supportingContent = { Text(stringResource(Res.string.lock_timeline_short_description)) },
					trailingContent = {
						Switch(
							timelineLocked,
							onCheckedChange = { blockingSettings.putBoolean("timeline_locked", it) }
						)
					},
					modifier = Modifier.clickable {
						blockingSettings.putBoolean("timeline_locked", !timelineLocked)
					},
					colors = ListItemDefaults.colors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
				)
			}
		}
		item {
			val warnWhenPostingPublicly by settings.getBooleanFlow("warn_when_posting_publicly", false)
				.collectAsStateWithLifecycle(false)

			Card(
				modifier = Modifier.listItemClip(2, 6).padding(bottom = 2.dp),
				shape = ListItemShape(2, 6),
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
				modifier = Modifier.listItemClip(3, 6).padding(bottom = 2.dp),
				shape = ListItemShape(3, 6),
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
		item {
			val disableAttachmentsDownload by settings.getBooleanFlow("disable_attachments_download", false)
				.collectAsStateWithLifecycle(false)
			
			Card(
				modifier = Modifier.listItemClip(4, 6).padding(bottom = 10.dp),
				shape = ListItemShape(4, 6),
			) {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.disable_attachments_download)) },
					trailingContent = {
						Switch(
							disableAttachmentsDownload,
							onCheckedChange = { blockingSettings.putBoolean("disable_attachments_download", it) }
						)
					},
					modifier = Modifier.clickable {
						blockingSettings.putBoolean("disable_attachments_download", !disableAttachmentsDownload)
					},
					colors = ListItemDefaults.colors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
				)
			}
		}
		item {
			val useGroupedNotifications by settings.getBooleanFlow("use_grouped_notifications", false)
				.collectAsStateWithLifecycle(false)

			Card(
				modifier = Modifier.listItemClip(5, 6).padding(bottom = 10.dp),
				shape = ListItemShape(5, 6),
			) {
				ListItem(
					headlineContent = { Text("grouped") },
					trailingContent = {
						Switch(
							useGroupedNotifications,
							onCheckedChange = { blockingSettings.putBoolean("use_grouped_notifications", it) }
						)
					},
					modifier = Modifier.clickable {
						blockingSettings.putBoolean("use_grouped_notifications", !useGroupedNotifications)
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
				modifier = Modifier.listItemClip(0, 3).padding(bottom = 2.dp),
				shape = ListItemShape(0, 3),
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

			Card(
				modifier = Modifier.listItemClip(1, 3).padding(bottom = 2.dp),
				shape = ListItemShape(1, 3),
			) {
				ListItem(
					headlineContent = {
						Text(
							translation(
								Res.string.posts_per_page,
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

			Card(
				modifier = Modifier.listItemClip(2, 3), // add padding
				shape = ListItemShape(2, 3),
			) {
				ListItem(
					headlineContent = {
						Text(
							translation(
								Res.string.notifs_per_page,
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
