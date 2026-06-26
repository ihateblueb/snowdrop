package site.remlit.snowdrop.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import site.remlit.snowdrop.ProfileRoute
import site.remlit.snowdrop.model.Notification
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.extension.toRelativeString
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_add_24px
import snowdrop.shared.generated.resources.icon_edit_24px
import snowdrop.shared.generated.resources.icon_notifications_active_24
import snowdrop.shared.generated.resources.icon_repeat_24px
import snowdrop.shared.generated.resources.icon_poll_24px
import snowdrop.shared.generated.resources.icon_star_24px
import snowdrop.shared.generated.resources.icon_tooth_24px

@Composable
fun Notification(notification: Notification) {
	val navHandler = LocalNavController.current

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
					}

					Row(
						modifier = Modifier
							.clickable(onClick = {
								navHandler.navigate(ProfileRoute(notification.account.id))
							})
					) {
						Avatar(notification.account, small = true)
					}

					Row(
						horizontalArrangement = Arrangement.spacedBy(5.dp),
						verticalAlignment = Alignment.CenterVertically
					) {
						var displayName by remember { mutableStateOf("") }
						var message by remember { mutableStateOf("") }

						when (notification.type) {
							"favourite", "pleroma:emoji_reaction", "reblog", "update", "status", "bite" ->
								displayName = notification.account.displayName
									?: notification.account.username
						}

						when (notification.type) {
							"favourite" -> message = "liked your post"
							"pleroma:emoji_reaction" -> message = "reacted with ${notification.emoji}"
							"reblog" -> message = "boosted your post"
							"update" -> message = "edited a post"
							"poll" -> message = "A poll you have voted in has ended"
							"status" -> message = "just posted"
							"bite" -> message = if (notification.bite?.biteBack == true) "bit you back"
								else if (notification.status != null) "bit your post"
								else "bit you"
						}

						val timestamp = "${notification.getCreatedAtTimestamp()?.toRelativeString()}"

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

						Text(
							text = title,
							modifier = Modifier.weight(1f),
							maxLines = 1,
							overflow = TextOverflow.Ellipsis
						)

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
			}

			HorizontalDivider(
				thickness = 1.dp,
				color = MaterialTheme.colorScheme.surfaceContainer
			)
		}
	}
}
