package site.remlit.snowdrop.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.toRoute
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.ComposeRoute
import site.remlit.snowdrop.ProfileRoute
import site.remlit.snowdrop.StatusInteractionDetailRoute
import site.remlit.snowdrop.ThreadRoute
import site.remlit.snowdrop.api.statuses.bookmarkStatus
import site.remlit.snowdrop.api.statuses.favouriteStatus
import site.remlit.snowdrop.api.statuses.reactToStatus
import site.remlit.snowdrop.api.statuses.reblogStatus
import site.remlit.snowdrop.api.statuses.unbookmarkStatus
import site.remlit.snowdrop.api.statuses.unfavouriteStatus
import site.remlit.snowdrop.api.statuses.unreactFromStatus
import site.remlit.snowdrop.api.statuses.unreblogStatus
import site.remlit.snowdrop.component.dropdown.DangerDropdownItem
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.model.Account
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Platform
import site.remlit.snowdrop.util.BoostColor
import site.remlit.snowdrop.util.LikeColor
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.SnackbarController
import site.remlit.snowdrop.util.WarningColor25
import site.remlit.snowdrop.util.atRoute
import site.remlit.snowdrop.util.bgIO
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.extension.isUnicodeEmoji
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow
import site.remlit.snowdrop.util.settings
import site.remlit.snowdrop.util.extension.toFormatShort
import site.remlit.snowdrop.util.extension.toRelativeString
import site.remlit.snowdrop.util.getFeature
import site.remlit.snowdrop.util.getPlatform
import site.remlit.snowdrop.util.vibrate
import site.remlit.snowdrop.view.InteractionViewType
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.bookmark
import snowdrop.shared.generated.resources.boosted
import snowdrop.shared.generated.resources.copy_link
import snowdrop.shared.generated.resources.delete
import snowdrop.shared.generated.resources.edit
import snowdrop.shared.generated.resources.hide_content
import snowdrop.shared.generated.resources.icon_add_24px
import snowdrop.shared.generated.resources.icon_bookmark_24px
import snowdrop.shared.generated.resources.icon_bookmark_filled_24px
import snowdrop.shared.generated.resources.icon_delete_24px
import snowdrop.shared.generated.resources.icon_edit_24px
import snowdrop.shared.generated.resources.icon_flag_24px
import snowdrop.shared.generated.resources.icon_image_24
import snowdrop.shared.generated.resources.icon_link_24px
import snowdrop.shared.generated.resources.icon_lock_24px
import snowdrop.shared.generated.resources.icon_mood_24px
import snowdrop.shared.generated.resources.icon_more_horiz_24px
import snowdrop.shared.generated.resources.icon_open_in_new_24px
import snowdrop.shared.generated.resources.icon_repeat_24px
import snowdrop.shared.generated.resources.icon_reply_24px
import snowdrop.shared.generated.resources.icon_reply_all_24px
import snowdrop.shared.generated.resources.icon_star_24px
import snowdrop.shared.generated.resources.icon_star_filled_24px
import snowdrop.shared.generated.resources.icon_volume_off_24px
import snowdrop.shared.generated.resources.icon_warning_24px
import snowdrop.shared.generated.resources.mute
import snowdrop.shared.generated.resources.open_in_browser
import snowdrop.shared.generated.resources.report
import snowdrop.shared.generated.resources.show_boosts
import snowdrop.shared.generated.resources.show_content
import snowdrop.shared.generated.resources.show_likes
import snowdrop.shared.generated.resources.show_reactions
import snowdrop.shared.generated.resources.unbookmark

@Composable
@OptIn(ExperimentalSettingsApi::class)
fun Status(status: Status) {
	val navHandler = LocalNavController.current
	val currentDest = navHandler.currentDestination
	val snackbarController = SnackbarController.current
	// TODO: update to LocalClipboard when this issue is resolved https://youtrack.jetbrains.com/issue/CMP-7624
	val clipboardManager = LocalClipboardManager.current
	val uriHandler = LocalUriHandler.current
	val haptics = LocalHapticFeedback.current
	val focusManager = LocalFocusManager.current
	val coroutineScope = rememberCoroutineScope()

	/* Preferences */
	val hideInteractionCounters by settings.getBooleanFlow("hide_interaction_counters", false)
		.collectAsStateWithLifecycle(false)

	/* View variables */
	val currentAccount by getCurrentAccountObjectFlow().collectAsStateWithLifecycle(null)
	var showEmojiPicker by remember { mutableStateOf(false) }

	var realStatus by remember { mutableStateOf(status) }
	var isReblog by remember { mutableStateOf(false) }
	var rebloggingAccount by remember { mutableStateOf<Account?>(null) }
	var isMine by remember { mutableStateOf(false) }
	//todo: or is admin? figure out how to do that

	if (status.reblog != null) {
		realStatus = status.reblog!!
		isReblog = true
		rebloggingAccount = status.account
	}

	if (realStatus.account?.id == currentAccount?.id)
		isMine = true

	var cwOpen by remember { mutableStateOf(false) }
	var showDropdown by remember { mutableStateOf(false) }

	var inThreadView by remember { mutableStateOf(false) }
	var threadViewMainStatus by remember { mutableStateOf(false) }

	inThreadView = atRoute<ThreadRoute>(currentDest)
	threadViewMainStatus = inThreadView && navHandler.currentBackStackEntry
		?.toRoute<ThreadRoute>()?.id == realStatus.id


	@Composable
	fun FooterButton(
		onClick: () -> Unit,
		colors: ButtonColors? = null,
		content: @Composable () -> Unit
	) {
		TextButton(
			onClick = onClick,
			colors = colors ?: ButtonDefaults.textButtonColors()
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(10.dp)
			) { content() }
		}
	}


	Column(
		modifier = Modifier.clickable(
			enabled = !inThreadView || (inThreadView && !threadViewMainStatus),
			onClick = { navHandler.navigate(ThreadRoute(realStatus.id)) }
		).background(
			if (threadViewMainStatus) MaterialTheme.colorScheme.surfaceContainerLow
			else Color.Unspecified
		)
	) {
		Column(
			modifier = Modifier.fillMaxWidth()
				.padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
			// todo: not vertically centered correctly
		) {
			if (isReblog && rebloggingAccount != null) {
				Row(
					modifier = Modifier.padding(start = 35.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					Icon(
						painterResource(Res.drawable.icon_repeat_24px),
						null,
						modifier = Modifier.padding(end = 5.dp),
						tint = MaterialTheme.colorScheme.secondary
					)
					Row(
						modifier = Modifier.weight(1f, fill = false),
						horizontalArrangement = Arrangement.spacedBy(5.dp),
						verticalAlignment = Alignment.CenterVertically
					) {
						Text(
							rebloggingAccount!!.displayName(),
							color = MaterialTheme.colorScheme.secondary,
							fontSize = 14.sp,
							fontWeight = FontWeight.Medium,
							overflow = TextOverflow.Ellipsis,
							maxLines = 1,
							modifier = Modifier.weight(1f, fill = false)
						)
						Text(
							stringResource(Res.string.boosted),
							color = MaterialTheme.colorScheme.secondary,
							fontSize = 14.sp,
							fontWeight = FontWeight.Medium
						)
					}
				}
			}

			/*
			* Header
			*/
			Row(
				modifier = Modifier.padding(5.dp)
					.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically
			) {
				// todo: this needs to be clipped since it's casting the clickable effect outside of the avatar boundaries
				Column(
					modifier = Modifier.padding(end = 10.dp)
						.clickable(onClick = {
							navHandler.navigate(ProfileRoute(realStatus.account!!.id))
						})
				) {
					Avatar(
						realStatus.account!!,
						small = inThreadView && !threadViewMainStatus,
					)
				}

				Column(
					modifier = Modifier.weight(1f)
						.padding(end = 10.dp)
				) {
					Text(
						realStatus.account!!.displayName(),
						fontWeight = FontWeight.Medium,
						overflow = TextOverflow.Ellipsis,
						maxLines = 1,
						modifier = Modifier.clickable(onClick = {
							navHandler.navigate(ProfileRoute(realStatus.account?.id!!))
						})
					)
					Text(
						"@${realStatus.account?.acct}",
						overflow = TextOverflow.Ellipsis,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						fontSize = 13.sp,
						maxLines = 1,
						modifier = Modifier.clickable(onClick = {
							navHandler.navigate(ProfileRoute(realStatus.account?.id!!))
						})
					)
				}

				Column(
					horizontalAlignment = Alignment.End
				) {
					Column(
						horizontalAlignment = Alignment.CenterHorizontally
					) {
						Visibility(status.visibility!!)
						Text(
							"${realStatus.getCreatedAtTimestamp()?.toRelativeString()}",
							fontSize = 13.sp
						)
					}
				}
			}


			/*
			*
			*  Content
			*
			*/

			@Composable
			fun renderContent() {
				if (realStatus.content != null) {
					HtmlContent(realStatus.content!!, mentions = realStatus.mentions)
				}
			}

			Column(modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)) {
				if (realStatus.spoilerText != null && !realStatus.spoilerText!!.isBlank()) {
					Column(
						modifier = Modifier.fillMaxWidth()
							.clip(RoundedCornerShape(10.dp))
							.background(WarningColor25)
							.clickable(onClick = {
								cwOpen = !cwOpen
							})
					) {
						Row(
							modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 10.dp)
								.fillMaxWidth(),
							horizontalArrangement = Arrangement.spacedBy(10.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							Icon(painterResource(Res.drawable.icon_warning_24px), null)

							Column {
								Text(
									realStatus.spoilerText!!,
									fontWeight = FontWeight.Medium
								)
								Text(
									if (!cwOpen) stringResource(Res.string.show_content)
									else stringResource(Res.string.hide_content),
									fontSize = 12.sp
								)
							}

							Spacer(Modifier.weight(1f))

							if (realStatus.mediaAttachments.isNotEmpty()) {
								Icon(painterResource(Res.drawable.icon_image_24), null)
							}
						}
					}

					AnimatedVisibility(cwOpen) {
						Column(
							modifier = Modifier.padding(top = 10.dp)
						) {
							renderContent()
						}
					}
				} else renderContent()

			}

			if (realStatus.quotedStatus != null) {
				MiniStatus(realStatus.quotedStatus!!)
			}

			/*
			* Reactions
			*/
			if (getFeature("reactions") && !realStatus.reactions.isEmpty()) {
				LazyRow(
					horizontalArrangement = Arrangement.spacedBy(5.dp)
				) {
					realStatus.reactions.forEach {
						item {
							TooltipBox(
								positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
									TooltipAnchorPosition.Above
								),
								tooltip = {
									if (isUnicodeEmoji(it.name)) return@TooltipBox
									PlainTooltip { Text(":${it.name}:") }
								},
								state = rememberTooltipState()
							) {
								OutlinedButton(
									onClick = {
										vibrate(!it.me, haptics)

										val tempName = if (isUnicodeEmoji(it.name)) it.name else ":${it.name}:"
										if (it.me) {
											coroutineScope.launch {
												val res = unreactFromStatus(realStatus.id, tempName)
												realStatus = res.response!!
												if (isReblog) status.reblog = res.response
											}
										} else if (!it.name.contains("@")) {
											coroutineScope.launch {
												val res = reactToStatus(realStatus.id, tempName)
												realStatus = res.response!!
												if (isReblog) status.reblog = res.response
											}
										} else {
											coroutineScope.launch {
												snackbarController.showSnackbar("You cannot react with a remote emoji")
											}
										}
									},
									contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
									colors = ButtonColors(
										containerColor = if (it.me) MaterialTheme.colorScheme.secondaryContainer
											else Color.Transparent,
										contentColor = if (it.me) MaterialTheme.colorScheme.secondary
											else ButtonDefaults.outlinedButtonColors().contentColor,
										disabledContainerColor = ButtonDefaults.outlinedButtonColors().disabledContainerColor,
										disabledContentColor = ButtonDefaults.outlinedButtonColors().disabledContentColor
									)
								) {
									Row(
										horizontalArrangement = Arrangement.spacedBy(5.dp),
										verticalAlignment = Alignment.CenterVertically
									) {
										val emoji = it.toEmoji()
										if (emoji != null) Emoji(emoji) else when (getPlatform()) {
											Platform.ANDROID -> Text(it.name)
											Platform.IOS -> Text(it.name, fontSize = 18.sp)
										}

										if (!blockingSettings.getBoolean("hide_interaction_counters", false))
											Text("${it.count}")
									}
								}
							}
						}
					}
				}
			}

			/*
			*
			*
			* Footer
			*
			*
			*/
			Row(
				modifier = Modifier.padding(start = 5.dp, end = 5.dp),
				horizontalArrangement = Arrangement.spacedBy(5.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				FooterButton(onClick = {
					navHandler.navigate(ComposeRoute(
						inReplyToId = realStatus.id,
						cw = if (!realStatus.spoilerText.isNullOrBlank()) "RE: ${realStatus.spoilerText}" else "",
						// what a block
						content = (if (!isMine) "@${realStatus.account!!.acct} " else "") +
							realStatus.mentions.filter { it.id != currentAccount?.id }.joinToString(separator = "") { "@${it.acct} " },
						visibility = realStatus.visibility
					))
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
						if (!isMine && realStatus.visibility != "public" && realStatus.visibility != "unlisted")
							return@c

						vibrate(!realStatus.reblogged, haptics)

						bgIO {
							val res: ApiResponse<Status> = if (realStatus.reblogged) unreblogStatus(realStatus.id)
							else reblogStatus(realStatus.id)
							if (res.error || res.response == null) {
								res.handleError(snackbarController)
								return@bgIO
							}

							// returns the status created for reblog, ooorr not sometimes.
							realStatus = res.response.reblog ?: res.response
							if (isReblog) status.reblog = res.response.reblog ?: res.response
						}
					},
					colors = if (realStatus.reblogged) ButtonDefaults.textButtonColors(
						contentColor = BoostColor
					) else null
				) {
					if (isMine || realStatus.visibility == "public" || realStatus.visibility == "unlisted") {
						if (realStatus.reblogged) Icon(
							painterResource(Res.drawable.icon_repeat_24px),
							null,
							tint = BoostColor
						) else Icon(
							painterResource(Res.drawable.icon_repeat_24px),
							null
						)

						if (!hideInteractionCounters)
							Text(realStatus.reblogsCount.toFormatShort())
					} else	 {
						Icon(
							painterResource(Res.drawable.icon_lock_24px),
							null
						)
					}
				}

				FooterButton(
					onClick = {
						vibrate(!realStatus.favourited, haptics)

						bgIO {
							val res: ApiResponse<Status> = if (realStatus.favourited) unfavouriteStatus(realStatus.id)
							else favouriteStatus(realStatus.id)
							if (res.error || res.response == null) {
								res.handleError(snackbarController)
								return@bgIO
							}
							realStatus = res.response
							if (isReblog) status.reblog = res.response
						}
					},
					colors = if (realStatus.favourited) ButtonDefaults.textButtonColors(
						contentColor = LikeColor
					) else null
				) {
					if (realStatus.favourited) Icon(
						painterResource(Res.drawable.icon_star_filled_24px),
						null,
						tint = LikeColor
					) else Icon(
						painterResource(Res.drawable.icon_star_24px),
						null
					)

					if (!hideInteractionCounters)
						Text(realStatus.favouritesCount.toFormatShort())
				}

				if (getFeature("reactions")) {
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

					DropdownMenu(
						expanded = showDropdown,
						onDismissRequest = { showDropdown = false }
					) {
						if (status.url != null) {
							DropdownMenuItem(
								text = { Text(stringResource(Res.string.copy_link)) },
								leadingIcon = {
									Icon(painterResource(Res.drawable.icon_link_24px), null)
								},
								onClick = {
									clipboardManager.setText(AnnotatedString(realStatus.url!!))
									showDropdown = !showDropdown
								}
							)

							DropdownMenuItem(
								text = { Text(stringResource(Res.string.open_in_browser)) },
								leadingIcon = {
									Icon(painterResource(Res.drawable.icon_open_in_new_24px), null)
								},
								onClick = {
									uriHandler.openUri(realStatus.url!!)
									showDropdown = !showDropdown
								}
							)
						}

						if (realStatus.bookmarked) {
							DropdownMenuItem(
								text = { Text(stringResource(Res.string.unbookmark)) },
								leadingIcon = {
									Icon(painterResource(Res.drawable.icon_bookmark_filled_24px), null)
								},
								onClick = {
									coroutineScope.launch {
										val res = unbookmarkStatus(realStatus.id)
										realStatus = res.response!!
										if (isReblog)
											status.reblog = res.response

										showDropdown = !showDropdown
									}
								}
							)
						} else {
							DropdownMenuItem(
								text = { Text(stringResource(Res.string.bookmark)) },
								leadingIcon = {
									Icon(painterResource(Res.drawable.icon_bookmark_24px), null)
								},
								onClick = {
									coroutineScope.launch {
										val res = bookmarkStatus(realStatus.id)
										realStatus = res.response!!
										if (isReblog)
											status.reblog = res.response

										showDropdown = !showDropdown
									}
								}
							)
						}

						HorizontalDivider()

						DropdownMenuItem(
							text = { Text(stringResource(Res.string.show_boosts)) },
							leadingIcon = {
								Icon(painterResource(Res.drawable.icon_repeat_24px), null)
							},
							onClick = {
								navHandler.navigate(StatusInteractionDetailRoute(realStatus.id, InteractionViewType.Boost.toString()))
							}
						)

						DropdownMenuItem(
							text = { Text(stringResource(Res.string.show_likes)) },
							leadingIcon = {
								Icon(painterResource(Res.drawable.icon_star_24px), null)
							},
							onClick = {
								navHandler.navigate(StatusInteractionDetailRoute(realStatus.id, InteractionViewType.Like.toString()))
							}
						)

						if (getFeature("reactions"))
							DropdownMenuItem(
								text = { Text(stringResource(Res.string.show_reactions)) },
								leadingIcon = {
									Icon(painterResource(Res.drawable.icon_mood_24px), null)
								},
								onClick = {
									navHandler.navigate(StatusInteractionDetailRoute(realStatus.id, InteractionViewType.Reaction.toString()))
								}
							)

						HorizontalDivider()

						DropdownMenuItem(
							text = { Text(stringResource(Res.string.mute)) },
							leadingIcon = {
								Icon(painterResource(Res.drawable.icon_volume_off_24px), null)
							},
							onClick = { }
						)

						DangerDropdownItem(
							text = { Text(stringResource(Res.string.report)) },
							leadingIcon = {
								Icon(painterResource(Res.drawable.icon_flag_24px), null)
							},
							onClick = { }
						)

						// if mine
						if (isMine) {
							HorizontalDivider()

							DropdownMenuItem(
								text = { Text(stringResource(Res.string.edit)) },
								leadingIcon = {
									Icon(painterResource(Res.drawable.icon_edit_24px), null)
								},
								onClick = { }
							)

							DangerDropdownItem(
								text = { Text(stringResource(Res.string.delete)) },
								leadingIcon = {
									Icon(painterResource(Res.drawable.icon_delete_24px), null)
								},
								onClick = { }
							)
						}
					}
				}
			}
		}

		HorizontalDivider(
			thickness = 1.dp,
			color = MaterialTheme.colorScheme.surfaceContainer
		)

		EmojiPicker(
			visible = showEmojiPicker,
			onDismiss = { showEmojiPicker = !showEmojiPicker },
			onSelectEmoji = {
				coroutineScope.launch {
					showEmojiPicker = !showEmojiPicker

					val res = reactToStatus(realStatus.id, ":${it.shortcode}:")
					if (isReblog)
						status.reblog = res.response
					else
						realStatus = res.response!!
				}
			}
		)
	}
}
