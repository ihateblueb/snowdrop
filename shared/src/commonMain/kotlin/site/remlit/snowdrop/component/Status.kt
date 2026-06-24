package site.remlit.snowdrop.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import org.jetbrains.compose.resources.painterResource
import site.remlit.snowdrop.Profile
import site.remlit.snowdrop.component.dropdown.DangerDropdownItem
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.model.User
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_add_24px
import snowdrop.shared.generated.resources.icon_bookmark_24px
import snowdrop.shared.generated.resources.icon_bookmark_filled_24px
import snowdrop.shared.generated.resources.icon_delete_24px
import snowdrop.shared.generated.resources.icon_edit_24px
import snowdrop.shared.generated.resources.icon_flag_24px
import snowdrop.shared.generated.resources.icon_link_24px
import snowdrop.shared.generated.resources.icon_more_horiz_24px
import snowdrop.shared.generated.resources.icon_open_in_new_24px
import snowdrop.shared.generated.resources.icon_repeat_24px
import snowdrop.shared.generated.resources.icon_reply_24px
import snowdrop.shared.generated.resources.icon_reply_all_24px
import snowdrop.shared.generated.resources.icon_star_24px
import snowdrop.shared.generated.resources.icon_star_filled_24px
import snowdrop.shared.generated.resources.icon_volume_off_24px

@Composable
fun Status(status: Status) {
	val navHandler = LocalNavController.current
	// TODO: update to LocalClipboard when this issue is resolved https://youtrack.jetbrains.com/issue/CMP-7624
	val clipboardManager = LocalClipboardManager.current
	val uriHandler = LocalUriHandler.current

	val currentAccount by getCurrentAccountObjectFlow().collectAsStateWithLifecycle(null)

	var realStatus by remember { mutableStateOf(status) }
	var isReblog by remember { mutableStateOf(false) }
	var rebloggingAccount by remember { mutableStateOf<User?>(null) }
	var isMine by remember { mutableStateOf(realStatus.account.id == currentAccount?.id) }
	//todo: or is admin? figure out how to do that

	if (status.reblog != null) {
		realStatus = status.reblog
		isReblog = true
		rebloggingAccount = status.account
	}

	var showDropdown by remember { mutableStateOf(false) }


	@Composable
	fun FooterButton(
		onClick: () -> Unit,
		content: @Composable () -> Unit
	) {
		TextButton(
			onClick = onClick,
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(10.dp)
			) { content() }
		}
	}


	Column(
		modifier = Modifier.fillMaxWidth()
			.padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
		// todo: not vertically centered correctly
	) {
		if (isReblog && rebloggingAccount != null) {
			Row(modifier = Modifier.padding(start = 35.dp)) {
				Icon(
					painterResource(Res.drawable.icon_repeat_24px),
					null,
					modifier = Modifier.padding(end = 5.dp),
					tint = MaterialTheme.colorScheme.secondary
				)
				Text(
					"${rebloggingAccount!!.displayName ?: rebloggingAccount!!.username} boosted",
					color = MaterialTheme.colorScheme.secondary
				)
			}
		}

		// Header
		Row(
			modifier = Modifier.padding(10.dp)
				.clickable(onClick = {
					navHandler.navigate(Profile(realStatus.account.id))
				}),
			verticalAlignment = Alignment.CenterVertically
		) {
			Column(modifier = Modifier.padding(end = 10.dp)) {
				Avatar(realStatus.account)
			}

			Column {
				Text(
					realStatus.account.displayName ?: realStatus.account.username,
					fontWeight = FontWeight.Medium,
				)
				Text("@${realStatus.account.fqn}")
			}

			// todo: visiblity, timestamp, etc.
			Column {

			}
		}

		// Content
		Column(modifier = Modifier.padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp)) {
			if (realStatus.content != null) {
				HtmlContent(realStatus.content!!, realStatus.mentions)
			}
		}

		if (realStatus.reactions.isEmpty()) {
			Row(
				horizontalArrangement = Arrangement.spacedBy(5.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				for (reaction in realStatus.reactions) {
					Text("${reaction.name} ${reaction.count}")
				}
			}
		}

		// Footer
		Row(
			horizontalArrangement = Arrangement.spacedBy(5.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			FooterButton(onClick = { }) {
				if (realStatus.inReplyToId != null) Icon(
					painterResource(Res.drawable.icon_reply_all_24px),
					null
				) else Icon(
					painterResource(Res.drawable.icon_reply_24px),
					null
				)

				Text("${realStatus.repliesCount}")
			}

			FooterButton(onClick = { }) {
				if (realStatus.reblogged) Icon(
					painterResource(Res.drawable.icon_repeat_24px),
					null,
					tint = MaterialTheme.colorScheme.primary
				) else Icon(
					painterResource(Res.drawable.icon_repeat_24px),
					null
				)

				Text("${realStatus.reblogsCount}")
			}

			FooterButton(onClick = { }) {
				if (realStatus.favourited) Icon(
					painterResource(Res.drawable.icon_star_filled_24px),
					null,
					tint = MaterialTheme.colorScheme.primary
				) else Icon(
					painterResource(Res.drawable.icon_star_24px),
					null
				)

				Text("${realStatus.favouritesCount}")
			}

			FooterButton(onClick = { }) {
				if (realStatus.favourited) Icon(
					painterResource(Res.drawable.icon_add_24px),
					null,
					tint = MaterialTheme.colorScheme.primary
				) else Icon(
					painterResource(Res.drawable.icon_add_24px),
					null
				)
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
					DropdownMenuItem(
						text = { Text("Copy link") },
						leadingIcon = {
							Icon(painterResource(Res.drawable.icon_link_24px), null)
						},
						onClick = {
							clipboardManager.setText(AnnotatedString(status.url))
							showDropdown = !showDropdown
						}
					)

					DropdownMenuItem(
						text = { Text("Open in browser") },
						leadingIcon = {
							Icon(painterResource(Res.drawable.icon_open_in_new_24px), null)
						},
						onClick = {
							uriHandler.openUri(status.url)
							showDropdown = !showDropdown
						}
					)

					HorizontalDivider()

					if (realStatus.bookmarked) {
						DropdownMenuItem(
							text = { Text("Unbookmark") },
							leadingIcon = {
								Icon(painterResource(Res.drawable.icon_bookmark_filled_24px), null)
							},
							onClick = { }
						)
					} else {
						DropdownMenuItem(
							text = { Text("Bookmark") },
							leadingIcon = {
								Icon(painterResource(Res.drawable.icon_bookmark_24px), null)
							},
							onClick = { }
						)
					}

					DropdownMenuItem(
						text = { Text("Mute") },
						leadingIcon = {
							Icon(painterResource(Res.drawable.icon_volume_off_24px), null)
						},
						onClick = { }
					)

					DangerDropdownItem(
						text = { Text("Report") },
						leadingIcon = {
							Icon(painterResource(Res.drawable.icon_flag_24px), null)
						},
						onClick = { }
					)

					// if mine
					if (isMine) {
						HorizontalDivider()

						DropdownMenuItem(
							text = { Text("Edit") },
							leadingIcon = {
								Icon(painterResource(Res.drawable.icon_edit_24px), null)
							},
							onClick = { }
						)

						DangerDropdownItem(
							text = { Text("Delete") },
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
}