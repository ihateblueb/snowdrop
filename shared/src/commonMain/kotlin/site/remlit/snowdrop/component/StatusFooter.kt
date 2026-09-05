package site.remlit.snowdrop.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.ComposeRoute
import site.remlit.snowdrop.StatusInteractionDetailRoute
import site.remlit.snowdrop.api.statuses.biteStatus
import site.remlit.snowdrop.api.statuses.bookmarkStatus
import site.remlit.snowdrop.api.statuses.deleteStatus
import site.remlit.snowdrop.api.statuses.favouriteStatus
import site.remlit.snowdrop.api.statuses.pinStatus
import site.remlit.snowdrop.api.statuses.reactToStatus
import site.remlit.snowdrop.api.statuses.reblogStatus
import site.remlit.snowdrop.api.statuses.unbookmarkStatus
import site.remlit.snowdrop.api.statuses.unfavouriteStatus
import site.remlit.snowdrop.api.statuses.unpinStatus
import site.remlit.snowdrop.api.statuses.unreblogStatus
import site.remlit.snowdrop.component.dropdown.DangerDropdownItem
import site.remlit.snowdrop.component.dropdown.MenuDivider
import site.remlit.snowdrop.component.dropdown.PreparedDropdownMenu
import site.remlit.snowdrop.model.Account
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.BoostColor
import site.remlit.snowdrop.util.LikeColor
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.LocalSnackbarController
import site.remlit.snowdrop.util.extension.toFormatShort
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow
import site.remlit.snowdrop.util.getFeature
import site.remlit.snowdrop.util.settings
import site.remlit.snowdrop.util.vibrate
import site.remlit.snowdrop.util.vibrateError
import site.remlit.snowdrop.util.vibrateSoft
import site.remlit.snowdrop.view.InteractionViewType
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.bite_post
import snowdrop.shared.generated.resources.bookmark
import snowdrop.shared.generated.resources.copy_link
import snowdrop.shared.generated.resources.delete
import snowdrop.shared.generated.resources.edit
import snowdrop.shared.generated.resources.icon_add_24px
import snowdrop.shared.generated.resources.icon_bookmark_24px
import snowdrop.shared.generated.resources.icon_bookmark_filled_24px
import snowdrop.shared.generated.resources.icon_delete_24px
import snowdrop.shared.generated.resources.icon_edit_24px
import snowdrop.shared.generated.resources.icon_keep_24px
import snowdrop.shared.generated.resources.icon_keep_off_24px
import snowdrop.shared.generated.resources.icon_link_24px
import snowdrop.shared.generated.resources.icon_lock_24px
import snowdrop.shared.generated.resources.icon_mood_24px
import snowdrop.shared.generated.resources.icon_more_horiz_24px
import snowdrop.shared.generated.resources.icon_open_in_new_24px
import snowdrop.shared.generated.resources.icon_outlined_flag_24px
import snowdrop.shared.generated.resources.icon_repeat_24px
import snowdrop.shared.generated.resources.icon_repeat_inner_fill_24px
import snowdrop.shared.generated.resources.icon_reply_24px
import snowdrop.shared.generated.resources.icon_reply_all_24px
import snowdrop.shared.generated.resources.icon_star_24px
import snowdrop.shared.generated.resources.icon_star_border_24px
import snowdrop.shared.generated.resources.icon_tooth_24px
import snowdrop.shared.generated.resources.icon_volume_off_24px
import snowdrop.shared.generated.resources.mute
import snowdrop.shared.generated.resources.open_in_browser
import snowdrop.shared.generated.resources.pin
import snowdrop.shared.generated.resources.report
import snowdrop.shared.generated.resources.show_boosts
import snowdrop.shared.generated.resources.show_likes
import snowdrop.shared.generated.resources.show_reactions
import snowdrop.shared.generated.resources.unbookmark
import snowdrop.shared.generated.resources.unpin

@OptIn(ExperimentalSettingsApi::class)
@Composable
fun StatusFooter(
	realStatus: Status,
	rebloggingAccount: Account?,
	isMine: Boolean,
	updateStatus: suspend (delete: Boolean) -> Unit,

	lockable: Boolean,
) {
	// TODO: update to LocalClipboard when this issue is resolved https://youtrack.jetbrains.com/issue/CMP-7624
	val clipboardManager = LocalClipboardManager.current
	val uriHandler = LocalUriHandler.current
	val focusManager = LocalFocusManager.current

	val navHandler = LocalNavController.current
	val snackbarController = LocalSnackbarController.current
	val haptics = LocalHapticFeedback.current
	val coroutineScope = rememberCoroutineScope()


	val currentAccount by remember { getCurrentAccountObjectFlow() }.collectAsStateWithLifecycle(null)
	var showEmojiPicker by remember { mutableStateOf(false) }

	var showDropdown by remember { mutableStateOf(false) }


	// Preferences
	val appendReOnReplies by settings.getBooleanFlow("append_re_on_replies", true)
		.collectAsStateWithLifecycle(true)

	val timelineLocked by settings.getBooleanFlow("timeline_locked", false)
		.collectAsStateWithLifecycle(false)

	val hideInteractionCounters by settings.getBooleanFlow("hide_interaction_counters", false)
		.collectAsStateWithLifecycle(false)


	@Composable
	fun FooterButton(
		onClick: () -> Unit,
		colors: ButtonColors? = null,
		enabled: Boolean? = true,
		content: @Composable () -> Unit
	) {
		TextButton(
			onClick = onClick,
			colors = colors ?: ButtonDefaults.textButtonColors(),
			enabled = (!lockable || !timelineLocked) && enabled == true
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(10.dp)
			) { content() }
		}
	}


	Row(
		modifier = Modifier.padding(start = 5.dp, end = 5.dp),
		horizontalArrangement = Arrangement.spacedBy(5.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		FooterButton(onClick = {
			navHandler.navigate(
				ComposeRoute(
					inReplyToId = realStatus.id,
					cw = if (!realStatus.spoilerText.isNullOrBlank()) {
						if (appendReOnReplies && !realStatus.spoilerText.lowercase().startsWith("re: "))
							"RE: ${realStatus.spoilerText}" else realStatus.spoilerText
					} else "",
					// what a block
					content = (if (!isMine) "@${realStatus.account!!.acct} " else "") +
						realStatus.mentions.filter { it.id != currentAccount?.id }
							.joinToString(separator = "") { "@${it.acct} " },
					visibility = realStatus.visibility
				)
			)
		}) {
			if (realStatus.inReplyToId != null) Icon(
				painterResource(Res.drawable.icon_reply_all_24px),
				null
			) else Icon(
				painterResource(Res.drawable.icon_reply_24px),
				null
			)

			if (!hideInteractionCounters)
				Text(realStatus.repliesCount.toFormatShort())
		}

		FooterButton(
			onClick = c@{
				vibrate(!realStatus.reblogged, haptics)

				coroutineScope.launch {
					val res: ApiResponse<Status> = if (realStatus.reblogged) unreblogStatus(realStatus.id)
					else reblogStatus(realStatus.id)
					if (res.error || res.response == null) {
						res.handleError(snackbarController)
						vibrateError(haptics)
						return@launch
					}

					if (rebloggingAccount?.id == currentAccount?.id) updateStatus(true)
					else updateStatus(false)
				}
			},
			colors = if (realStatus.reblogged) ButtonDefaults.textButtonColors(
				contentColor = BoostColor()
			) else null,
			enabled = (isMine && realStatus.visibility != "direct") || realStatus.visibility == "public" || realStatus.visibility == "unlisted" || realStatus.visibility == "local"
		) {
			if ((isMine && realStatus.visibility != "direct") || realStatus.visibility == "public" || realStatus.visibility == "unlisted" || realStatus.visibility == "local") {
				if (realStatus.reblogged) Icon(
					painterResource(Res.drawable.icon_repeat_inner_fill_24px),
					null,
					tint = BoostColor()
				) else Icon(
					painterResource(Res.drawable.icon_repeat_24px),
					null
				)

				if (!hideInteractionCounters)
					Text(realStatus.reblogsCount.toFormatShort())
			} else {
				Icon(
					painterResource(Res.drawable.icon_lock_24px),
					null
				)
			}
		}

		FooterButton(
			onClick = {
				vibrate(!realStatus.favourited, haptics)

				coroutineScope.launch {
					val res: ApiResponse<Status> = if (realStatus.favourited) unfavouriteStatus(realStatus.id)
					else favouriteStatus(realStatus.id)
					if (res.error || res.response == null) {
						res.handleError(snackbarController)
						vibrateError(haptics)
						return@launch
					}

					updateStatus(false)
				}
			},
			colors = if (realStatus.favourited) ButtonDefaults.textButtonColors(
				contentColor = LikeColor()
			) else null
		) {
			if (realStatus.favourited) Icon(
				painterResource(Res.drawable.icon_star_24px),
				null,
				tint = LikeColor()
			) else Icon(
				painterResource(Res.drawable.icon_star_border_24px),
				null
			)

			if (!hideInteractionCounters)
				Text(realStatus.favouritesCount.toFormatShort())
		}

		if (getFeature("reactions") || getFeature("reactions_pleroma")) {
			FooterButton(onClick = { showEmojiPicker = !showEmojiPicker; focusManager.clearFocus() }) {
				Icon(
					painterResource(Res.drawable.icon_add_24px),
					null
				)
			}
		}

		Box {
			FooterButton(onClick = { showDropdown = !showDropdown }) {
				Icon(
					painterResource(Res.drawable.icon_more_horiz_24px),
					null
				)
			}

			PreparedDropdownMenu(
				expanded = showDropdown,
				onDismissRequest = { showDropdown = false }
			) {
				if (realStatus.url != null) {
					DropdownMenuItem(
						text = { Text(stringResource(Res.string.copy_link)) },
						leadingIcon = {
							Icon(painterResource(Res.drawable.icon_link_24px), null)
						},
						shape = MenuDefaults.leadingItemShape,
						onClick = {
							coroutineScope.launch {
								clipboardManager.setText(AnnotatedString(realStatus.url))
								vibrateSoft(haptics)
								showDropdown = false
							}
						}
					)

					DropdownMenuItem(
						text = { Text(stringResource(Res.string.open_in_browser)) },
						leadingIcon = {
							Icon(painterResource(Res.drawable.icon_open_in_new_24px), null)
						},
						shape = MenuDefaults.middleItemShape,
						onClick = {
							uriHandler.openUri(realStatus.url)
							showDropdown = false
						}
					)
				}

				DropdownMenuItem(
					text = {
						if (!realStatus.bookmarked) Text(stringResource(Res.string.bookmark))
						else Text(stringResource(Res.string.unbookmark))
					},
					leadingIcon = {
						if (!realStatus.bookmarked) Icon(painterResource(Res.drawable.icon_bookmark_24px), null)
						else Icon(painterResource(Res.drawable.icon_bookmark_filled_24px), null)
					},
					shape = MenuDefaults.middleItemShape,
					onClick = {
						coroutineScope.launch {
							vibrate(true, haptics)

							val res = if (!realStatus.bookmarked) bookmarkStatus(realStatus.id) else unbookmarkStatus(realStatus.id)
							if (res.error || res.response == null) {
								res.handleError(snackbarController)
								vibrateError(haptics)
							}

							updateStatus(false)
							showDropdown = false
						}
					}
				)

				if (getFeature("biting") && !isMine) {
					DropdownMenuItem(
						text = { Text(stringResource(Res.string.bite_post)) },
						leadingIcon = {
							Icon(painterResource(Res.drawable.icon_tooth_24px), null)
						},
						shape = MenuDefaults.middleItemShape,
						onClick = {
							coroutineScope.launch {
								vibrate(true, haptics)

								val res = biteStatus(realStatus.id)
								if (res.error) {
									res.handleError(snackbarController)
									vibrateError(haptics)
								}

								showDropdown = false
							}
						}
					)
				}

				MenuDivider()

				DropdownMenuItem(
					text = { Text(stringResource(Res.string.show_boosts)) },
					leadingIcon = {
						Icon(painterResource(Res.drawable.icon_repeat_24px), null)
					},
					shape = MenuDefaults.middleItemShape,
					onClick = {
						navHandler.navigate(
							StatusInteractionDetailRoute(
								realStatus.id,
								InteractionViewType.Boost.toString()
							)
						)

						showDropdown = false
					}
				)

				DropdownMenuItem(
					text = { Text(stringResource(Res.string.show_likes)) },
					leadingIcon = {
						Icon(painterResource(Res.drawable.icon_star_border_24px), null)
					},
					shape = MenuDefaults.middleItemShape,
					onClick = {
						navHandler.navigate(
							StatusInteractionDetailRoute(
								realStatus.id,
								InteractionViewType.Like.toString()
							)
						)

						showDropdown = false
					}
				)

				if (getFeature("reactions") || getFeature("reactions_pleroma"))
					DropdownMenuItem(
						text = { Text(stringResource(Res.string.show_reactions)) },
						leadingIcon = {
							Icon(painterResource(Res.drawable.icon_mood_24px), null)
						},
						shape = MenuDefaults.middleItemShape,
						onClick = {
							navHandler.navigate(
								StatusInteractionDetailRoute(
									realStatus.id,
									InteractionViewType.Reaction.toString()
								)
							)

							showDropdown = false
						}
					)

				MenuDivider()

				DropdownMenuItem(
					text = { Text(stringResource(Res.string.mute)) },
					leadingIcon = {
						Icon(painterResource(Res.drawable.icon_volume_off_24px), null)
					},
					shape = MenuDefaults.middleItemShape,
					onClick = { }
				)

				DangerDropdownItem(
					text = { Text(stringResource(Res.string.report)) },
					leadingIcon = {
						Icon(painterResource(Res.drawable.icon_outlined_flag_24px), null)
					},
					shape = if (isMine) MenuDefaults.middleItemShape else MenuDefaults.trailingItemShape,
					onClick = { }
				)

				// if mine
				if (isMine) {
					MenuDivider()

					DropdownMenuItem(
						text = {
							if (!realStatus.pinned) Text(stringResource(Res.string.pin))
							else Text(stringResource(Res.string.unpin))
						},
						leadingIcon = {
							if (!realStatus.pinned) Icon(painterResource(Res.drawable.icon_keep_24px), null)
							else Icon(painterResource(Res.drawable.icon_keep_off_24px), null)
						},
						shape = MenuDefaults.middleItemShape,
						onClick = {
							coroutineScope.launch {
								vibrate(true, haptics)

								val res = if (!realStatus.pinned) pinStatus(realStatus.id)
								else unpinStatus(realStatus.id)
								if (res.error || res.response == null) {
									res.handleError(snackbarController)
									vibrateError(haptics)
								}

								updateStatus(false)
								showDropdown = false
							}
						}
					)

					DropdownMenuItem(
						text = { Text(stringResource(Res.string.edit)) },
						leadingIcon = {
							Icon(painterResource(Res.drawable.icon_edit_24px), null)
						},
						shape = MenuDefaults.middleItemShape,
						onClick = {
							navHandler.navigate(ComposeRoute(editingId = realStatus.id))
						}
					)

					DangerDropdownItem(
						text = { Text(stringResource(Res.string.delete)) },
						leadingIcon = {
							Icon(painterResource(Res.drawable.icon_delete_24px), null)
						},
						shape = MenuDefaults.trailingItemShape,
						onClick = {
							coroutineScope.launch {
								vibrate(true, haptics)

								val req = deleteStatus(realStatus.id)
								if (req.error) {
									req.handleError(snackbarController)
									vibrateError(haptics)
									return@launch
								}

								updateStatus(true)
							}
						}
					)
				}
			}
		}
	}

	EmojiPicker(
		visible = showEmojiPicker,
		onDismiss = { showEmojiPicker = !showEmojiPicker },
		onSelectEmoji = {
			coroutineScope.launch {
				showEmojiPicker = !showEmojiPicker

				val res = reactToStatus(realStatus.id, it.shortcode)
				if (res.error || res.response == null) {
					res.handleError(snackbarController)
					vibrateError(haptics)
					return@launch
				}

				updateStatus(false)
			}
		},
		onEnterUnicodeEmoji = {
			coroutineScope.launch {
				showEmojiPicker = !showEmojiPicker

				val res = reactToStatus(realStatus.id, it)
				if (res.error || res.response == null) {
					res.handleError(snackbarController)
					vibrateError(haptics)
					return@launch
				}

				updateStatus(false)
			}
		}
	)
}
