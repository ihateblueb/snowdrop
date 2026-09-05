package site.remlit.snowdrop.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.GridFlow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.toRoute
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.ComposeRoute
import site.remlit.snowdrop.ProfileRoute
import site.remlit.snowdrop.StatusInteractionDetailRoute
import site.remlit.snowdrop.StatusMediaAttachmentRoute
import site.remlit.snowdrop.ThreadRoute
import site.remlit.snowdrop.api.statuses.biteStatus
import site.remlit.snowdrop.api.statuses.bookmarkStatus
import site.remlit.snowdrop.api.statuses.deleteStatus
import site.remlit.snowdrop.api.statuses.favouriteStatus
import site.remlit.snowdrop.api.statuses.getStatus
import site.remlit.snowdrop.api.statuses.pinStatus
import site.remlit.snowdrop.api.statuses.reactToStatus
import site.remlit.snowdrop.api.statuses.reblogStatus
import site.remlit.snowdrop.api.statuses.unbookmarkStatus
import site.remlit.snowdrop.api.statuses.unfavouriteStatus
import site.remlit.snowdrop.api.statuses.unpinStatus
import site.remlit.snowdrop.api.statuses.unreactFromStatus
import site.remlit.snowdrop.api.statuses.unreblogStatus
import site.remlit.snowdrop.component.dropdown.DangerDropdownItem
import site.remlit.snowdrop.component.dropdown.MenuDivider
import site.remlit.snowdrop.component.dropdown.PreparedDropdownMenu
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.model.Account
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.util.BoostColor
import site.remlit.snowdrop.util.LikeColor
import site.remlit.snowdrop.util.LocalStatusStateController
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.LocalSnackbarController
import site.remlit.snowdrop.util.WarningColor25
import site.remlit.snowdrop.util.annotatedString.mapEmojisToInlineTextContent
import site.remlit.snowdrop.util.annotatedString.withAccountLink
import site.remlit.snowdrop.util.annotatedString.withEmojis
import site.remlit.snowdrop.util.atRoute
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.cache.fetchAccountOrNull
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow
import site.remlit.snowdrop.util.settings
import site.remlit.snowdrop.util.extension.toFormatShort
import site.remlit.snowdrop.util.extension.toRelativeString
import site.remlit.snowdrop.util.getFeature
import site.remlit.snowdrop.util.translation
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
import snowdrop.shared.generated.resources.filtered_by_x
import snowdrop.shared.generated.resources.hide_content
import snowdrop.shared.generated.resources.icon_add_24px
import snowdrop.shared.generated.resources.icon_bookmark_24px
import snowdrop.shared.generated.resources.icon_bookmark_filled_24px
import snowdrop.shared.generated.resources.icon_delete_24px
import snowdrop.shared.generated.resources.icon_edit_24px
import snowdrop.shared.generated.resources.icon_filter_alt_24px
import snowdrop.shared.generated.resources.icon_image_24px
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
import snowdrop.shared.generated.resources.icon_reply_20px
import snowdrop.shared.generated.resources.icon_reply_24px
import snowdrop.shared.generated.resources.icon_reply_all_24px
import snowdrop.shared.generated.resources.icon_star_24px
import snowdrop.shared.generated.resources.icon_star_border_24px
import snowdrop.shared.generated.resources.icon_tooth_24px
import snowdrop.shared.generated.resources.icon_volume_off_24px
import snowdrop.shared.generated.resources.icon_warning_24px
import snowdrop.shared.generated.resources.mute
import snowdrop.shared.generated.resources.open_in_browser
import snowdrop.shared.generated.resources.pin
import snowdrop.shared.generated.resources.pinned
import snowdrop.shared.generated.resources.post_by_x
import snowdrop.shared.generated.resources.replying_to_self
import snowdrop.shared.generated.resources.replying_to_x
import snowdrop.shared.generated.resources.report
import snowdrop.shared.generated.resources.show_boosts
import snowdrop.shared.generated.resources.show_content
import snowdrop.shared.generated.resources.show_likes
import snowdrop.shared.generated.resources.show_reactions
import snowdrop.shared.generated.resources.unbookmark
import snowdrop.shared.generated.resources.unpin
import snowdrop.shared.generated.resources.x_boosted
import snowdrop.shared.generated.resources.you_cannot_react_with_a_remote_emoji
import kotlin.math.ceil
import kotlin.time.Duration.Companion.seconds

/**
 * Status element
 *
 * @param status Status to be shown
 *
 * @since 0.0.1-alpha
 * */
@Composable
@OptIn(ExperimentalSettingsApi::class, ExperimentalGridApi::class)
fun Status(
	status: Status,
	onUpdate: (Status?) -> Unit,
	lockable: Boolean = false,
	pinned: Boolean = false,
	showDivider: Boolean = true,
	filterContext: String? = null
) {
	val navHandler = LocalNavController.current
	val currentDest = navHandler.currentDestination
	val snackbarController = LocalSnackbarController.current
	val haptics = LocalHapticFeedback.current
	val coroutineScope = rememberCoroutineScope()

	val statusStateController = LocalStatusStateController.current
	val cwState = statusStateController.cw
	val filteredState = statusStateController.filtered


	/* View variables */
	val currentAccount by remember { getCurrentAccountObjectFlow() }.collectAsStateWithLifecycle(null)

	var status by remember { mutableStateOf(status) }
	val realStatus = status.reblog ?: status
	val isReblog = status.reblog != null
	val rebloggingAccount = status.account
	var isMine by remember { mutableStateOf(false) }
	// todo: or is admin? figure out how to do that

	val replyingToAccount by remember { fetchAccountOrNull(realStatus.inReplyToAccountId, snackbarController) }
		.collectAsStateWithLifecycle(null)

	val applicableFilters = realStatus.filtered?.filter {
			filterContext != null && it.filter.context.contains(filterContext)
		}.orEmpty()
	val filtered = applicableFilters.isNotEmpty()
	val filterStateKey = "${filterContext ?: "none"}:${realStatus.id}"
	var isVisible by remember(filterStateKey, filtered) {
		mutableStateOf(
			!filtered || filteredState.getOrElse(filterStateKey) {
				statusStateController.defaultFilteredValue
			}
		)
	}
	val isHiddenFilter = filtered && applicableFilters.any { it.filter.filterAction == "hide" }

	LaunchedEffect(filteredState[filterStateKey], filterStateKey) {
		if (filtered) isVisible = filteredState.getOrElse(filterStateKey) {
			statusStateController.defaultFilteredValue
		}
	}


	if (realStatus.account?.id == currentAccount?.id)
		isMine = true

	if (!cwState.containsKey(realStatus.id))
		cwState[realStatus.id] = statusStateController.defaultCwValue

	if (!filteredState.containsKey(filterStateKey))
		filteredState[filterStateKey] = statusStateController.defaultFilteredValue

	suspend fun updateStatus(delete: Boolean = false) {
		if (delete) {
			isVisible = false
			return onUpdate(null)
		}

		val res = getStatus(status.id)
		if (res.error || res.response == null) {
			res.handleError(snackbarController)
			return
		}

		status = res.response
		onUpdate(res.response)
	}

	var inThreadView by remember { mutableStateOf(false) }
	var threadViewMainStatus by remember { mutableStateOf(false) }

	inThreadView = atRoute<ThreadRoute>(currentDest)
	threadViewMainStatus = inThreadView && navHandler.currentBackStackEntry
		?.toRoute<ThreadRoute>()?.id == realStatus.id

	var timestampKey by remember { mutableStateOf(0) }
	LaunchedEffect(Unit) {
		while (true) {
			delay(10.seconds)
			timestampKey++
		}
	}

	// start content
	Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
		//<editor-fold name="Filtered toggle">
		if (!isVisible && filtered && !isHiddenFilter) {
			Column(
				modifier = Modifier.fillMaxWidth()
					.clickable { filteredState[filterStateKey] = true }
			) {
				Row(
					modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 10.dp)
						.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(10.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					Icon(painterResource(Res.drawable.icon_filter_alt_24px), null)

					Column {
						Text(
							translation(
								Res.string.filtered_by_x,
								mapOf("filters" to AnnotatedString(
									applicableFilters.joinToString { "${it.filter.title}" }
								))
							)
						)
						Text(
							translation(Res.string.show_content),
							fontSize = 13.sp
						)
					}
				}

				Divider()
			}
		}
		//</editor-fold>

		if (isVisible) {
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
						.padding(top = 10.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
				) {

					Row(
						modifier = Modifier.padding(start = 35.dp)
					) {
						Column(
							verticalArrangement = Arrangement.spacedBy(5.dp)
						) {
							if (pinned && currentAccount != null) {
								Row(
									verticalAlignment = Alignment.CenterVertically
								) {
									Icon(
										painterResource(Res.drawable.icon_keep_24px),
										null,
										modifier = Modifier.padding(end = 5.dp),
										tint = MaterialTheme.colorScheme.secondary
									)
									Row(
										modifier = Modifier.weight(1f, fill = false),
										horizontalArrangement = Arrangement.spacedBy(5.dp),
										verticalAlignment = Alignment.CenterVertically
									) {
										val mappedEmojis = mapEmojisToInlineTextContent(currentAccount!!.emojis)
										Text(
											translation(Res.string.pinned),
											color = MaterialTheme.colorScheme.secondary,
											fontSize = 14.sp,
											fontWeight = FontWeight.Medium,
											inlineContent = mappedEmojis
										)
									}
								}
							}

							if (isReblog && rebloggingAccount != null) {
								Row(
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
										val mappedEmojis =
											mapEmojisToInlineTextContent(rebloggingAccount!!.emojis)
										Text(
											translation(
												Res.string.x_boosted,
												mapOf("clickable_display_name" to buildAnnotatedString {
													withStyle(
														style = SpanStyle(
															color = MaterialTheme.colorScheme.secondary,
															fontSize = 14.sp,
														)
													) { withAccountLink(rebloggingAccount!!) }
												}.withEmojis(mappedEmojis))
											),
											color = MaterialTheme.colorScheme.secondary,
											fontSize = 14.sp,
											fontWeight = FontWeight.Medium,
											inlineContent = mappedEmojis
										)
									}
								}
							}
						}
					}

					/*
					* Header
					*/
					val __translation_post_by_x = translation(Res.string.post_by_x, mapOf("display_name" to AnnotatedString(realStatus.account!!.displayName()))).text
					Row(
						modifier = Modifier.padding(5.dp)
							.fillMaxWidth()
							.semantics { contentDescription = __translation_post_by_x },
						verticalAlignment = Alignment.CenterVertically
					) {
						// todo: this needs to be clipped since it's casting the clickable effect outside of the avatar boundaries
						Column(
							modifier = Modifier.padding(end = 10.dp)
								.clickable(onClick = {
									navHandler.navigate(ProfileRoute(realStatus.account!!.id))
								}).semantics { hideFromAccessibility() }
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
							HtmlContent(
								realStatus.account!!.displayName(),
								emojis = realStatus.account!!.emojis,
								fontWeight = FontWeight.Medium,
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
								Visibility(realStatus.visibility!!)

								Row(
									verticalAlignment = Alignment.CenterVertically
								) {
									key(timestampKey) {
										Text(
											"${realStatus.getCreatedAtTimestamp()?.toRelativeString(short = true)}",
											fontSize = 13.sp
										)
									}
									if (realStatus.editedAt != null) {
										Text(
											"*",
											color = MaterialTheme.colorScheme.onSurfaceVariant,
											fontSize = 13.sp
										)
									}
								}
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
						Column(
							verticalArrangement = Arrangement.spacedBy(10.dp)
						) {
							Column(
								verticalArrangement = Arrangement.spacedBy(5.dp)
							) {
								if (realStatus.inReplyToId != null) {
									Row(
										horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)
									) {
										Icon(painterResource(Res.drawable.icon_reply_20px), null)

										if (realStatus.inReplyToAccountId == realStatus.account!!.id) {
											Text(
												translation(Res.string.replying_to_self),
												fontSize = 13.sp
											)
										} else {
											Text(
												translation(
													Res.string.replying_to_x,
													mapOf(
														"handle" to if (replyingToAccount != null)
															AnnotatedString("@${replyingToAccount!!.acct}")
																.withAccountLink(replyingToAccount!!)
														else AnnotatedString("...")
													)
												),
												fontSize = 13.sp
											)
										}
									}
								}

								if (!realStatus.content.isNullOrBlank()) {
									if (threadViewMainStatus) {
										SelectionContainer {
											HtmlContent(
												string = realStatus.content!!,
												mentions = realStatus.mentions,
												emojis = realStatus.emojis,
												emojiSize = 1.5.em,
												showEmojiTooltips = false // will cause a crash if we show emoji tooltips
											)
										}
									} else {
										HtmlContent(
											string = realStatus.content!!,
											mentions = realStatus.mentions,
											emojis = realStatus.emojis,
											emojiSize = 1.5.em
										)
									}
								}
							}

							if (realStatus.mediaAttachments.isNotEmpty()) {
								Grid({
									// its 1:30am so this is probably not ideal, and the bottom in an uneven(3)
									// grid should expand to full width

									if (realStatus.mediaAttachments.size < 2) column(1f)
									else repeat(2) { column(0.5f) }

									if (realStatus.mediaAttachments.size < 2) row(1f)
									else repeat(ceil(realStatus.mediaAttachments.size.toDouble() / 2).toInt()) {
										row(0.5f)
									}

									flow = GridFlow.Row
									gap(5.dp)
								}) {
									realStatus.mediaAttachments.forEach { media ->
										StatusMediaAttachment(
											media,
											includeFallback = true,
											modifier = Modifier.height(200.dp),
											onClick = {
												navHandler.navigate(
													StatusMediaAttachmentRoute(
														realStatus.id,
														realStatus.mediaAttachments.indexOf(media)
													)
												)
											}
										)
									}
								}
							}

							if (realStatus.poll != null) Poll(realStatus)

							val quote = realStatus.quote ?: realStatus.quotedStatus
							if (quote?.quotedStatus != null) {
								MiniStatus(quote.quotedStatus)
							}
						}
					}

					Column(modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)) {
						if (realStatus.spoilerText != null && realStatus.spoilerText!!.isNotBlank()) {
							Column(
								modifier = Modifier.fillMaxWidth()
									.clip(RoundedCornerShape(10.dp))
									.background(WarningColor25)
									.clickable(onClick = {
										cwState[realStatus.id] = !cwState.getOrElse(realStatus.id) { false }
									})
							) {
								Row(
									modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 10.dp)
										.fillMaxWidth(),
									horizontalArrangement = Arrangement.spacedBy(10.dp),
									verticalAlignment = Alignment.CenterVertically
								) {
									Icon(painterResource(Res.drawable.icon_warning_24px), null)

									Column(modifier = Modifier.weight(1f)) {
										HtmlContent(
											realStatus.spoilerText!!,
											emojis = realStatus.emojis,
											fontWeight = FontWeight.Medium
										)
										Text(
											if (!cwState.getOrElse(realStatus.id) { false }) stringResource(Res.string.show_content)
											else stringResource(Res.string.hide_content),
											fontSize = 12.sp
										)
									}

									if (realStatus.mediaAttachments.isNotEmpty()) {
										Icon(painterResource(Res.drawable.icon_image_24px), null)
									}
								}
							}

							AnimatedVisibility(cwState.getOrElse(realStatus.id) { false }) {
								Column(
									modifier = Modifier.padding(top = 10.dp)
								) {
									renderContent()
								}
							}
						} else renderContent()

					}

					/*
					*
					* Reactions
					*
					*/
					if ((getFeature("reactions") && realStatus.reactions.isNotEmpty()) || (getFeature("reactions_pleroma") && realStatus.pleroma?.reactions?.isNotEmpty() == true)) {
						val cannotUseRemoteEmojiMessage = stringResource(Res.string.you_cannot_react_with_a_remote_emoji)
						val reactions = if (getFeature("reactions_pleroma")) realStatus.pleroma!!.reactions else realStatus.reactions
						LazyRow(
							contentPadding = PaddingValues(horizontal = 5.dp), //todo: redo all the padding on this entire component
							horizontalArrangement = Arrangement.spacedBy(5.dp),
						) {
							reactions.forEach {
								item {
									TooltipBox(
										positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
											TooltipAnchorPosition.Above
										),
										tooltip = {
											if (it.url != null) PlainTooltip { Text(":${it.name}:") }
										},
										state = rememberTooltipState()
									) {
										OutlinedButton(
											onClick = {
												vibrate(!it.me, haptics)

												coroutineScope.launch {
													if (it.me || !it.name.contains("@")) {
														val res = if (it.me) unreactFromStatus(realStatus.id, it.name)
														else reactToStatus(realStatus.id, it.name)
														if (res.error || res.response == null) {
															res.handleError(snackbarController)
															return@launch
														}

														updateStatus()
													} else {
														snackbarController.showSnackbar(cannotUseRemoteEmojiMessage)
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
												Reaction(it, showTooltip = false)

												if (!blockingSettings.getBoolean("hide_interaction_counters", false))
													Text("${it.count}")
											}
										}
									}
								}
							}
						}
					}

					StatusFooter(
						realStatus = realStatus,
						rebloggingAccount = rebloggingAccount,
						isMine = isMine,
						updateStatus = { delete -> updateStatus(delete) },
						lockable = lockable
					)
				}

				if (showDivider) Divider()
			}
		}
	}
}
