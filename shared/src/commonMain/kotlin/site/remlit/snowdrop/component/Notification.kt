package site.remlit.snowdrop.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.ProfileRoute
import site.remlit.snowdrop.api.followRequest.authorizeFollowRequest
import site.remlit.snowdrop.api.followRequest.rejectFollowRequest
import site.remlit.snowdrop.model.Notification
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.bgIO
import site.remlit.snowdrop.util.extension.toRelativeString
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.a_poll_you_have_voted_in_has_ended
import snowdrop.shared.generated.resources.accept
import snowdrop.shared.generated.resources.bit_you
import snowdrop.shared.generated.resources.bit_you_back
import snowdrop.shared.generated.resources.bit_your_post
import snowdrop.shared.generated.resources.boosted_your_post
import snowdrop.shared.generated.resources.edited_a_post
import snowdrop.shared.generated.resources.followed_you
import snowdrop.shared.generated.resources.icon_add_24px
import snowdrop.shared.generated.resources.icon_check_24px
import snowdrop.shared.generated.resources.icon_close_24px
import snowdrop.shared.generated.resources.icon_edit_24px
import snowdrop.shared.generated.resources.icon_notifications_active_24
import snowdrop.shared.generated.resources.icon_person_add_24px
import snowdrop.shared.generated.resources.icon_repeat_24px
import snowdrop.shared.generated.resources.icon_poll_24px
import snowdrop.shared.generated.resources.icon_star_24px
import snowdrop.shared.generated.resources.icon_tooth_24px
import snowdrop.shared.generated.resources.just_posted
import snowdrop.shared.generated.resources.liked_your_post
import snowdrop.shared.generated.resources.reacted_with_x
import snowdrop.shared.generated.resources.reject
import snowdrop.shared.generated.resources.requested_to_follow_you

@Composable
fun Notification(notification: Notification) {
	val navHandler = LocalNavController.current

	// sharkey doesn't include the actual reactions in the notifications for some reason
	// chuckya includes the reaction prop, so we should use that. otherwise there's no point in showing the notif
	if (notification.type == "reaction" && notification.reaction == null && notification.emoji == null) return

	if (notification.type == "mention" && notification.status != null) {
		Status(notification.status)
	} else {
		Column {
			Column(
				modifier = Modifier.padding(15.dp)
					.fillMaxWidth()
			) {
				Row(
					horizontalArrangement = Arrangement.spacedBy(10.dp)
				) {
					when (notification.type) {
						"favourite" -> Icon(
							painterResource(Res.drawable.icon_star_24px), null,
							tint = MaterialTheme.colorScheme.primary
						)
						"pleroma:emoji_reaction" -> Icon(
							painterResource(Res.drawable.icon_add_24px), null,
							tint = MaterialTheme.colorScheme.primary
						)
						"reaction" -> Icon(
							painterResource(Res.drawable.icon_add_24px), null,
							tint = MaterialTheme.colorScheme.primary
						)
						"reblog" -> Icon(
							painterResource(Res.drawable.icon_repeat_24px), null,
							tint = MaterialTheme.colorScheme.primary
						)
						"update" -> Icon(
							painterResource(Res.drawable.icon_edit_24px), null,
							tint = MaterialTheme.colorScheme.primary
						)
						"poll" -> Icon(
							painterResource(Res.drawable.icon_poll_24px), null,
							tint = MaterialTheme.colorScheme.primary
						)
						"status" -> Icon(
							painterResource(Res.drawable.icon_notifications_active_24), null,
							tint = MaterialTheme.colorScheme.primary
						)
						"bite" -> Icon( // todo: tooth, and test bite notifs
							painterResource(Res.drawable.icon_tooth_24px), null,
							tint = MaterialTheme.colorScheme.primary
						)
						"follow_request" -> Icon(
							painterResource(Res.drawable.icon_person_add_24px), null,
							tint = MaterialTheme.colorScheme.primary
						)
						"follow" -> Icon(
							painterResource(Res.drawable.icon_person_add_24px), null,
							tint = MaterialTheme.colorScheme.primary
						)
					}

					Row(
						modifier = Modifier
							.clickable(onClick = {
								navHandler.navigate(ProfileRoute(notification.account.id))
							})
					) {
						Avatar(notification.account, smaller = true)
					}

					Row(
						horizontalArrangement = Arrangement.spacedBy(5.dp),
						verticalAlignment = Alignment.CenterVertically
					) {
						var displayName by remember { mutableStateOf("") }
						var message by remember { mutableStateOf("") }

						when (notification.type) {
							"favourite", "pleroma:emoji_reaction", "reaction", "reblog", "update", "status", "bite",
								"follow_request", "follow" ->
									displayName = notification.account.displayName()
						}

						when (notification.type) {
							"favourite" -> message = stringResource(Res.string.liked_your_post)
							"pleroma:emoji_reaction" -> message = stringResource(Res.string.reacted_with_x, "${notification.emoji}")
							"reaction" -> message =
								if (notification.reaction == null) stringResource(Res.string.reacted_with_x, "${notification.emoji}")
								else stringResource(Res.string.reacted_with_x, ":${notification.reaction.name}:")
							"reblog" -> message = stringResource(Res.string.boosted_your_post)
							"update" -> message = stringResource(Res.string.edited_a_post)
							"poll" -> message = stringResource(Res.string.a_poll_you_have_voted_in_has_ended)
							"status" -> message = stringResource(Res.string.just_posted)
							"bite" -> message = if (notification.bite?.biteBack == true) stringResource(Res.string.bit_you_back)
								else if (notification.status != null) stringResource(Res.string.bit_your_post)
								else stringResource(Res.string.bit_you)
							"follow_request" -> message = stringResource(Res.string.requested_to_follow_you)
							"follow" -> message = stringResource(Res.string.followed_you)
						}

						/*
						* Actually render the link and style it and all that stuff
						*/
						val linkListener = LinkInteractionListener { link ->
							if (link is LinkAnnotation.Clickable) {
								navHandler.navigate(ProfileRoute(link.tag))
							}
						}

						val title = buildAnnotatedString {
							withLink(
								LinkAnnotation.Clickable(
									tag = notification.account.id,
									linkInteractionListener = linkListener,
									styles = TextLinkStyles(
										style = SpanStyle(
											textDecoration = TextDecoration.None,
											color = MaterialTheme.colorScheme.onBackground
										)
									)
								)
							) {
								withStyle(style = SpanStyle(
									fontWeight = FontWeight.Bold
								)) { append(displayName) }
							}

							append(" $message")
							toAnnotatedString()
						}

						// todo: make only display name not wrap
						Text(
							text = title,
							modifier = Modifier.weight(1f),
							lineHeight = with(LocalDensity.current) { smallerAvatarSize.dp.toSp() }
						)

						val timestamp = "${notification.getCreatedAtTimestamp()?.toRelativeString()}"

						Text(
							text = timestamp,
							fontSize = 13.sp,
							maxLines = 1
						)
					}
				}

				if (notification.status != null) {
					Column(
						modifier = Modifier.padding(top = 10.dp)
					) {
						MiniStatus(notification.status)
					}
				}

				if (notification.type == "follow_request") {
					Row(
						modifier = Modifier.padding(top = 10.dp),
						horizontalArrangement = Arrangement.spacedBy(10.dp)
					) {
						FilledTonalButton(onClick = { bgIO { authorizeFollowRequest(notification.account.id) } }) {
							Icon(painterResource(Res.drawable.icon_check_24px), null)
							Text(stringResource(Res.string.accept))
						}
						OutlinedButton(onClick = { bgIO { rejectFollowRequest(notification.account.id) } }) {
							Icon(painterResource(Res.drawable.icon_close_24px), null)
							Text(stringResource(Res.string.reject))
						}
					}
				}
			}

			HorizontalDivider(
				thickness = 1.dp,
				color = MaterialTheme.colorScheme.surfaceContainer
			)
		}
	}
}
