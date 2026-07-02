package site.remlit.snowdrop.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.russhwolf.settings.ExperimentalSettingsApi
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.ProfileRoute
import site.remlit.snowdrop.api.accounts.followAccount
import site.remlit.snowdrop.api.accounts.getRelationships
import site.remlit.snowdrop.api.accounts.unfollowAccount
import site.remlit.snowdrop.component.Avatar
import site.remlit.snowdrop.component.HtmlContent
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.component.bigAvatarRadius
import site.remlit.snowdrop.component.bigAvatarSize
import site.remlit.snowdrop.model.Relationship
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.SnackbarController
import site.remlit.snowdrop.util.atRoute
import site.remlit.snowdrop.util.bg
import site.remlit.snowdrop.util.bgIO
import site.remlit.snowdrop.util.cache.fetchAccount
import site.remlit.snowdrop.util.extension.formatNumber
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow
import site.remlit.snowdrop.util.settings
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources._continue
import snowdrop.shared.generated.resources.are_you_sure_you_want_to_cancel_your_follow_request
import snowdrop.shared.generated.resources.are_you_sure_you_want_to_send_a_follow_request
import snowdrop.shared.generated.resources.are_you_sure_you_want_to_unfollow
import snowdrop.shared.generated.resources.cancel
import snowdrop.shared.generated.resources.cancel_request
import snowdrop.shared.generated.resources.edit_profile
import snowdrop.shared.generated.resources.follow
import snowdrop.shared.generated.resources.followers
import snowdrop.shared.generated.resources.following
import snowdrop.shared.generated.resources.icon_arrow_back_24
import snowdrop.shared.generated.resources.joined_at_x
import snowdrop.shared.generated.resources.media
import snowdrop.shared.generated.resources.posts
import snowdrop.shared.generated.resources.posts_and_replies
import snowdrop.shared.generated.resources.profile
import snowdrop.shared.generated.resources.request_to_follow
import snowdrop.shared.generated.resources.unfollow
import snowdrop.shared.generated.resources.x_posts
import snowdrop.shared.generated.resources.yes

const val headerHeight = 200

@Composable
@OptIn(ExperimentalSettingsApi::class)
fun ProfileView(id: String) = ViewSurface {
	val navHandler = LocalNavController.current
	val snackbarHandler = SnackbarController.current
	val currentDest = navHandler.currentDestination

	/* Preferences */
	val hideFollowCounters by settings.getBooleanFlow("hide_follow_counters", false)
		.collectAsStateWithLifecycle(false)

	/* View variables */
	val currentAccount by getCurrentAccountObjectFlow()
		.collectAsStateWithLifecycle(null)

	val account by fetchAccount(id, snackbarHandler)
		.collectAsStateWithLifecycle(null)

	var isMe by remember { mutableStateOf(false) }
	if (currentAccount != null && currentAccount?.id == account?.id)
		isMe = true

	val scrollState = rememberScrollState()

	var relationship by remember { mutableStateOf<Relationship?>(null) }
	if (!isMe && account != null) bgIO {
		val res = getRelationships(listOf(account!!.id))
		if (res.error || res.response == null) {
			res.handleError(snackbarHandler)
			return@bgIO
		}
		relationship = res.response.first()
	}

	val verticalOffset = (-((bigAvatarSize/2) - 4)).dp
	var selectedTab by remember { mutableStateOf(0) }

	Column {
		TopAppBar(
			navigationIcon = {
				// not sure why you can't just check isMe.. if you do it just doesn't ever show up
				//
				// re: because then clicking on yourself from a status will act like MyProfile when it isn't the
				//     MyProfile page, it shouldn't do that.
				if (atRoute<ProfileRoute>(currentDest)) {
					IconButton(onClick = { navHandler.popBackStack() }) {
						Icon(painterResource(Res.drawable.icon_arrow_back_24), null)
					}
				}
			},
			title = {
				if (account == null) Column {
					Text(stringResource(Res.string.profile))
					Text(
						stringResource(Res.string.x_posts, "0"),
						fontSize = 14.sp
					)
				} else Column {
					Text(
						account!!.displayName(),
						maxLines = 1,
						overflow = TextOverflow.Ellipsis
					)
					Text(
						stringResource(Res.string.x_posts, formatNumber(account!!.statusesCount)),
						fontSize = 14.sp
					)
				}
			}
		)

		if (account == null) {
			Column(
				modifier = Modifier.fillMaxHeight().fillMaxWidth(),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Center
			) {
				CircularProgressIndicator()
			}
		} else {
			Column(
				modifier = Modifier
					.verticalScroll(scrollState)
			) {
				Column {
					@Composable
					fun fallbackHeader() {
						Box(
							modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)
								.height(headerHeight.dp)
								.fillMaxWidth()
						)
					}

					if (account!!.header != null) {
						KamelImage(
							resource = { asyncPainterResource(account!!.headerStatic ?: account!!.header!!) },
							contentDescription = account!!.headerDescription,
							contentScale = ContentScale.Crop,
							onLoading = { fallbackHeader() },
							modifier = Modifier.height(headerHeight.dp)
								.fillMaxWidth(),
						)
					} else fallbackHeader()

					// The Rest
					Column(
						modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 0.dp, bottom = 15.dp)
							.offset(y = verticalOffset)
					) {
						// top of header, avatar and button
						Row(
							modifier = Modifier.padding(bottom = 10.dp)
								.fillMaxWidth(),

							verticalAlignment = Alignment.Bottom
						) {
							// jank outer border
							Box(contentAlignment = Alignment.Center) {
								Box(
									modifier = Modifier.background(
										MaterialTheme.colorScheme.background,
										RoundedCornerShape((bigAvatarRadius + 2).dp)
									).height((bigAvatarSize + 6).dp)
										.width((bigAvatarSize + 6).dp)
								)
								Avatar(account = account!!, big = true)
							}

							Row(
								modifier = Modifier.fillMaxWidth(),
								horizontalArrangement = Arrangement.End
							) {
								Row {
									fun follow() = bg {
										val res = followAccount(account!!.id)
										if (res.error || res.response == null) {
											res.handleError(snackbarHandler)
											return@bg
										}
										relationship = res.response
									}

									fun unfollow() = bg {
										val res = unfollowAccount(account!!.id)
										if (res.error || res.response == null) {
											res.handleError(snackbarHandler)
											return@bg
										}
										relationship = res.response
									}

									var showRelationshipActionWarning by remember { mutableStateOf(false) }

									if (showRelationshipActionWarning)
										AlertDialog(
											text = {
												if (relationship!!.following || relationship!!.requested) {
													if (relationship!!.requested) Text(stringResource(Res.string.are_you_sure_you_want_to_cancel_your_follow_request, account!!.acct))
													else Text(stringResource(Res.string.are_you_sure_you_want_to_unfollow, account!!.acct))
												} else {
													if (account!!.locked) Text(stringResource(Res.string.are_you_sure_you_want_to_send_a_follow_request, account!!.acct))
												}
											},
											dismissButton = {
												TextButton(
													onClick = { showRelationshipActionWarning = !showRelationshipActionWarning }
												) {
													Text(stringResource(Res.string.cancel))
												}
											},
											confirmButton = {
												TextButton(
													onClick = {
														if (relationship!!.following || relationship!!.requested) unfollow()
														else follow()

														showRelationshipActionWarning = !showRelationshipActionWarning
													}
												) {
													Text(stringResource(Res.string.yes))
												}
											},
											onDismissRequest = { showRelationshipActionWarning = !showRelationshipActionWarning },
											modifier = Modifier,
										)

									if (isMe) {
										OutlinedButton(onClick = {
											bg { snackbarHandler.showSnackbar("Not implemented") }
										}) {
											Text(stringResource(Res.string.edit_profile))
										}
									} else if (relationship != null) {
										if (relationship!!.following || relationship!!.requested) {
											OutlinedButton(
												onClick = { showRelationshipActionWarning = !showRelationshipActionWarning },
												border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.error),
												colors = ButtonDefaults.outlinedButtonColors(
													contentColor = MaterialTheme.colorScheme.error
												)
											) {
												if (relationship!!.requested) Text(stringResource(Res.string.cancel_request))
												else Text(stringResource(Res.string.unfollow))
											}
										} else {
											FilledTonalButton(
												onClick = {
													// todo: add confirmation to follow- only if account is locked
													if (account!!.locked) {
														showRelationshipActionWarning = !showRelationshipActionWarning
													} else follow()
												}
											) {
												if (account!!.locked) Text(stringResource(Res.string.request_to_follow))
												else Text(stringResource(Res.string.follow))
											}
										}
									}
								}
							}
						}

						// display name
						Row {
							Column {
								Text(
									account!!.displayName(),
									fontWeight = FontWeight.Bold,
									fontSize = 24.sp
								)
								Text(
									"@${account!!.acct}",
									color = MaterialTheme.colorScheme.onSurface
								)
							}
						}

						// Bio
						if (account!!.note != null)
							Column(modifier = Modifier.padding(top = 10.dp)) { HtmlContent(account!!.note!!) }

						// Fields
						if (!account!!.fields.isEmpty())
							Column(
								modifier = Modifier.padding(top = 10.dp)
									.clip(RoundedCornerShape(10.dp))
									.border(1.dp, MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(10.dp))
									.background(MaterialTheme.colorScheme.surfaceContainer),
							) {
								Column(
									modifier = Modifier.padding(10.dp),
									verticalArrangement = Arrangement.spacedBy(5.dp)
								) {
									account!!.fields.forEach { (name, value) ->
										Row(
											horizontalArrangement = Arrangement.spacedBy(5.dp)
										) {
											Text(
												name,
												modifier = Modifier.weight(0.50f),
												color = MaterialTheme.colorScheme.primary
											)
											HtmlContent(
												value,
												modifier = Modifier.weight(1.75f)
											)
										}
									}
								}
							}

						Row(modifier = Modifier.padding(top = 10.dp)) {
							Text(
								stringResource(Res.string.joined_at_x, account!!.createdAt),
								color = MaterialTheme.colorScheme.onSurfaceVariant
							)
						}

						// bottom of header
						if (!hideFollowCounters)
							Row(
								modifier = Modifier.padding(top = 10.dp),
								horizontalArrangement = Arrangement.spacedBy(10.dp)
							) {
								Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
									Text(
										"${account!!.followersCount}",
										fontWeight = FontWeight.Bold
									)
									Text(stringResource(Res.string.followers))
								}
								Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
									Text(
										"${account!!.followingCount}",
										fontWeight = FontWeight.Bold
									)
									Text(stringResource(Res.string.following))
								}
							}
					}

					Column(
						modifier = Modifier.offset(y = verticalOffset)
					) {
						HorizontalDivider()

						PrimaryTabRow(selectedTabIndex = selectedTab) {
							Tab(selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text(stringResource(Res.string.posts)) })
							Tab(selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text(stringResource(Res.string.posts_and_replies)) })
							Tab(selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text(stringResource(Res.string.media)) })
						}

					}
					// stay inside this above column ^^^
					// if you don't, there's a weird bottom space caused by the offset
					// and other things. this fixes it.
				}
			}
		}
	}
}
