package site.remlit.snowdrop.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.russhwolf.settings.ExperimentalSettingsApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.api.statuses.createStatus
import site.remlit.snowdrop.component.Avatar
import site.remlit.snowdrop.component.EmojiPicker
import site.remlit.snowdrop.component.MiniStatus
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.component.Visibility
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.model.request.CreateStatusRequest
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.SnackbarController
import site.remlit.snowdrop.util.WarningColor25
import site.remlit.snowdrop.util.bgIO
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.cache.fetchStatus
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.compose
import snowdrop.shared.generated.resources.content_warning
import snowdrop.shared.generated.resources.icon_close_24px
import snowdrop.shared.generated.resources.icon_globe_20px
import snowdrop.shared.generated.resources.icon_home_20px
import snowdrop.shared.generated.resources.icon_lock_20px
import snowdrop.shared.generated.resources.icon_mail_20px
import snowdrop.shared.generated.resources.icon_mood_24px
import snowdrop.shared.generated.resources.icon_send_24px
import snowdrop.shared.generated.resources.icon_warning_24px
import snowdrop.shared.generated.resources.reply
import snowdrop.shared.generated.resources.visibility_direct
import snowdrop.shared.generated.resources.visibility_direct_description
import snowdrop.shared.generated.resources.visibility_followers
import snowdrop.shared.generated.resources.visibility_followers_description
import snowdrop.shared.generated.resources.visibility_public
import snowdrop.shared.generated.resources.visibility_public_description
import snowdrop.shared.generated.resources.visibility_unlisted
import snowdrop.shared.generated.resources.visibility_unlisted_description
import snowdrop.shared.generated.resources.write_your_post_here

@Composable
@OptIn(ExperimentalSettingsApi::class)
fun ComposeView(
	inReplyToId: String? = null,
	initialCw: String = "",
	initialContent: String = "",
	visibility: String? = null
) = ViewSurface {
	val navHandler = LocalNavController.current
	val snackbarHandler = SnackbarController.current
	val focusManager = LocalFocusManager.current

	val currentAccount by getCurrentAccountObjectFlow()
		.collectAsStateWithLifecycle(null)

	var canSubmit by remember { mutableStateOf(false) }

	var visibilityDropdownOpen by remember { mutableStateOf(false) }
	var showCwField by remember { mutableStateOf(false) }
	var showEmojiPicker by remember { mutableStateOf(false) }

	if (!initialCw.isBlank()) showCwField = true

	var cw by remember { mutableStateOf(initialCw) }
	var content by remember { mutableStateOf(initialContent) }
	var visibility by remember {
		mutableStateOf(visibility
			?: blockingSettings.getString("default_visibility", "public"))
	}

	var replyTarget by remember { mutableStateOf<Status?>(null) }
	LaunchedEffect(inReplyToId) {
		if (inReplyToId != null) fetchStatus(inReplyToId).collect {
			replyTarget = it
		}
	}

	// can submit stuff
	canSubmit = !content.isBlank()

	suspend fun sendPost() {
		val res = createStatus(CreateStatusRequest(
			inReplyToId = inReplyToId,
			status = content,
			spoilerText = cw,
			visibility = visibility
		))
		if (res.error || res.response == null) {
			res.handleError(snackbarHandler)
			return
		}
	}

	TopAppBar(
		navigationIcon = {
			IconButton(onClick = { navHandler.popBackStack() }) {
				Icon(painterResource(Res.drawable.icon_close_24px), null)
			}
		},
		title = {
			if (inReplyToId != null) Text(stringResource(Res.string.reply))
			else Text(stringResource(Res.string.compose))
		}
	)

	Box(
		modifier = Modifier.fillMaxSize()
	) {
		Column(
			modifier = Modifier.fillMaxSize()
		) {
			if (currentAccount != null) {
				Row(
					modifier = Modifier.padding(10.dp)
						.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(10.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					Avatar(currentAccount!!)

					Column(
						modifier = Modifier.weight(1f)
					) {
						Text(
							currentAccount!!.displayName(),
							fontWeight = FontWeight.Medium,
							overflow = TextOverflow.Ellipsis,
							maxLines = 1
						)
						Text(
							"@${currentAccount!!.acct}",
							overflow = TextOverflow.Ellipsis,
							color = MaterialTheme.colorScheme.onSurfaceVariant,
							fontSize = 13.sp,
							maxLines = 1
						)
					}

					Row(
						horizontalArrangement = Arrangement.End
					) {
						Row {
							TextButton(onClick = { visibilityDropdownOpen = !visibilityDropdownOpen }) {
								Visibility(visibility, true)
							}

							// Visibility picker
							DropdownMenu(
								expanded = visibilityDropdownOpen,
								onDismissRequest = { visibilityDropdownOpen = !visibilityDropdownOpen }
							) {
								// todo: do minimum visibility based on the view's visibility parameter
								DropdownMenuItem(
									leadingIcon = {
										Icon(painterResource(Res.drawable.icon_globe_20px) ,null)
									},
									text = {
										Column(modifier = Modifier.padding(vertical = 5.dp)) {
											Text(
												stringResource(Res.string.visibility_public),
												fontWeight = FontWeight.Medium
											)
											Text(
												stringResource(Res.string.visibility_public_description),
												fontSize = 13.sp
											)
										}
									},
									onClick = {
										visibility = "public"
										visibilityDropdownOpen = !visibilityDropdownOpen
									}
								)
								DropdownMenuItem(
									leadingIcon = {
										Icon(painterResource(Res.drawable.icon_home_20px) ,null)
									},
									text = {
										Column(modifier = Modifier.padding(vertical = 5.dp)) {
											Text(
												stringResource(Res.string.visibility_unlisted),
												fontWeight = FontWeight.Medium
											)
											Text(
												stringResource(Res.string.visibility_unlisted_description),
												fontSize = 13.sp
											)
										}
									},
									onClick = {
										visibility = "unlisted"
										visibilityDropdownOpen = !visibilityDropdownOpen
									}
								)
								DropdownMenuItem(
									leadingIcon = {
										Icon(painterResource(Res.drawable.icon_lock_20px) ,null)
									},
									text = {
										Column(modifier = Modifier.padding(vertical = 5.dp)) {
											Text(
												stringResource(Res.string.visibility_followers),
												fontWeight = FontWeight.Medium
											)
											Text(
												stringResource(Res.string.visibility_followers_description),
												fontSize = 13.sp
											)
										}
									},
									onClick = {
										visibility = "private"
										visibilityDropdownOpen = !visibilityDropdownOpen
									}
								)
								DropdownMenuItem(
									leadingIcon = {
										Icon(painterResource(Res.drawable.icon_mail_20px) ,null)
									},
									text = {
										Column(modifier = Modifier.padding(vertical = 5.dp)) {
											Text(
												stringResource(Res.string.visibility_direct),
												fontWeight = FontWeight.Medium
											)
											Text(
												stringResource(Res.string.visibility_direct_description),
												fontSize = 13.sp
											)
										}
									},
									onClick = {
										visibility = "direct"
										visibilityDropdownOpen = !visibilityDropdownOpen
									}
								)
							}
						}
					}
				}

				if (replyTarget != null)
					Column(
						modifier = Modifier.padding(horizontal = 10.dp)
					) {
						MiniStatus(replyTarget!!, showContentEvenIfCw = true)
					}


				Column(
					modifier = Modifier.fillMaxHeight().weight(1f),
					verticalArrangement = Arrangement.spacedBy(5.dp)
				) {
					AnimatedVisibility(
						visible = showCwField,
						enter = expandVertically(),
						exit = shrinkVertically()
					) {
						TextField(
							value = cw,
							onValueChange = { cw = it },
							placeholder = { Text(stringResource(Res.string.content_warning)) },
							modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, top = 5.dp)
								.clip(RoundedCornerShape(10.dp)),
							maxLines = 1,
							colors = TextFieldDefaults.colors(
								unfocusedContainerColor = WarningColor25,
								unfocusedIndicatorColor = Color(0x00000000),
								focusedContainerColor = WarningColor25,
								focusedIndicatorColor = Color(0x00000000)
							)
						)
					}

					TextField(
						value = content,
						placeholder = { Text(stringResource(Res.string.write_your_post_here)) },
						onValueChange = { content = it },
						modifier = Modifier.fillMaxWidth().fillMaxHeight(),
						colors = TextFieldDefaults.colors(
							unfocusedContainerColor = Color(0x00000000),
							unfocusedIndicatorColor = Color(0x00000000),
							focusedContainerColor = Color(0x00000000),
							focusedIndicatorColor = Color(0x00000000),
						)
					)
				}

				/*
				* Footer
				* */
				Row(
					modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)
						.padding(all = 5.dp)
						.fillMaxWidth()
						.imePadding(),
					verticalAlignment = Alignment.CenterVertically
				) {
					IconButton(onClick = { showCwField = !showCwField }) {
						Icon(painterResource(Res.drawable.icon_warning_24px), null)
					}
					IconButton(onClick = { showEmojiPicker = !showEmojiPicker; focusManager.clearFocus() }) {
						Icon(painterResource(Res.drawable.icon_mood_24px), null)
					}


					// End
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.End
					) {
						Row {
							FilledTonalIconButton(
								onClick = { bgIO { sendPost() }; navHandler.popBackStack() },
								enabled = canSubmit
							) {
								Icon(painterResource(Res.drawable.icon_send_24px), null)
							}
						}
					}
				}
			}
		}

		EmojiPicker(
			visible = showEmojiPicker,
			onDismiss = { showEmojiPicker = !showEmojiPicker },
			onSelectEmoji = { content += ":${it.shortcode}:" }
		)
	}
}
