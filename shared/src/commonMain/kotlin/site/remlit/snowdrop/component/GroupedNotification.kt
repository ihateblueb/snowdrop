package site.remlit.snowdrop.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.ProfileRoute
import site.remlit.snowdrop.api.biteBack
import site.remlit.snowdrop.api.followRequest.authorizeFollowRequest
import site.remlit.snowdrop.api.followRequest.ignoreFollowRequest
import site.remlit.snowdrop.api.followRequest.rejectFollowRequest
import site.remlit.snowdrop.model.Account
import site.remlit.snowdrop.model.GroupedNotificationsResults
import site.remlit.snowdrop.model.Notification
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.LocalSnackbarController
import site.remlit.snowdrop.util.annotatedString.htmlToAnnotatedString
import site.remlit.snowdrop.util.annotatedString.withAccountLink
import site.remlit.snowdrop.util.extension.toRelativeString
import site.remlit.snowdrop.util.getFeature
import site.remlit.snowdrop.util.translation
import site.remlit.snowdrop.util.vibrate
import site.remlit.snowdrop.util.vibrateError
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.a_poll_you_have_voted_in_has_ended
import snowdrop.shared.generated.resources.accept
import snowdrop.shared.generated.resources.bite_back
import snowdrop.shared.generated.resources.icon_block_24px
import snowdrop.shared.generated.resources.icon_check_24px
import snowdrop.shared.generated.resources.icon_close_24px
import snowdrop.shared.generated.resources.icon_edit_24px
import snowdrop.shared.generated.resources.icon_mood_24px
import snowdrop.shared.generated.resources.icon_notifications_active_24px
import snowdrop.shared.generated.resources.icon_person_add_24px
import snowdrop.shared.generated.resources.icon_repeat_24px
import snowdrop.shared.generated.resources.icon_poll_24px
import snowdrop.shared.generated.resources.icon_star_24px
import snowdrop.shared.generated.resources.icon_tooth_24px
import snowdrop.shared.generated.resources.ignore
import snowdrop.shared.generated.resources.reject
import snowdrop.shared.generated.resources.x_accepted_your_follow_request
import snowdrop.shared.generated.resources.x_bit_you
import snowdrop.shared.generated.resources.x_bit_you_back
import snowdrop.shared.generated.resources.x_bit_your_post
import snowdrop.shared.generated.resources.x_boosted_your_post
import snowdrop.shared.generated.resources.x_edited_a_post
import snowdrop.shared.generated.resources.x_followed_you
import snowdrop.shared.generated.resources.x_just_posted
import snowdrop.shared.generated.resources.x_liked_your_post
import snowdrop.shared.generated.resources.x_reacted_with_x
import snowdrop.shared.generated.resources.x_requested_to_follow_you
import kotlin.time.Duration.Companion.seconds

/**
 * Notification component.
 *
 * */
@Composable
fun GroupedNotification(
	group: GroupedNotificationsResults.NotificationGroup,
	accounts: List<Account>,
	statuses: List<Status>,
	onAction: () -> Unit = {}
) {
	val navHandler = LocalNavController.current
	val haptics = LocalHapticFeedback.current
	val snackbarController = LocalSnackbarController.current
	val coroutineScope = rememberCoroutineScope()

	// only shown once it's certain this notification type is supported
	var show by remember { mutableStateOf(false) }

	var translationKey by remember { mutableStateOf<StringResource?>(null) }
	val replacementMap = remember { mutableStateMapOf<String, AnnotatedString>() }

	val suggestedAccount = accounts.find { it.id == group.sampleAccountIds.first() }
	val relevantStatus = statuses.find { it.id == group.statusId }

	//<editor-fold name="If display name should be shown">
	val (displayNameAnnotatedString, displayNameEmojiMapping) = htmlToAnnotatedString(suggestedAccount!!.displayName(), emojis = suggestedAccount.emojis)
	when (group.type) {
		"favourite", "reaction", "reblog", "update", "status", "follow_request", "follow", "follow_request_accepted" ->
			replacementMap["clickable_display_name"] = displayNameAnnotatedString
				.withAccountLink(suggestedAccount)
	}
	//</editor-fold>

	//<editor-fold name="Notification message">
	when (group.type) {
		"favourite" -> translationKey = Res.string.x_liked_your_post
		"reaction" -> {
			translationKey = Res.string.x_reacted_with_x
			replacementMap["emoji"] = AnnotatedString(
				(if (group.reaction == null) "" // egh
				else if (group.reaction.url != null) ":${group.reaction.name}:"
				else group.reaction.name) ?: ""
			)
		}
		"reblog" -> translationKey = Res.string.x_boosted_your_post
		"update" -> translationKey = Res.string.x_edited_a_post
		"poll" -> translationKey = Res.string.a_poll_you_have_voted_in_has_ended
		"status" -> translationKey = Res.string.x_just_posted
		"follow_request" -> translationKey = Res.string.x_requested_to_follow_you
		"follow" -> translationKey = Res.string.x_followed_you
		"follow_request_accepted" -> translationKey = Res.string.x_accepted_your_follow_request
	}
	//</editor-fold>

	// show toggle on point
	if (translationKey != null) show = true

	if (group.type == "mention" && relevantStatus != null) {
		Status(relevantStatus, {})
	} else if (show) {
		Column {
			Column(
				modifier = Modifier.padding(15.dp)
					.fillMaxWidth()
			) {
				//<editor-fold name="Notification header">
				Row(
					horizontalArrangement = Arrangement.spacedBy(10.dp)
				) {
					when (group.type) {
						"favourite" -> Icon(
							painterResource(Res.drawable.icon_star_24px), null,
							tint = MaterialTheme.colorScheme.primary
						)
						"pleroma:emoji_reaction", "reaction" -> Icon(
							painterResource(Res.drawable.icon_mood_24px), null,
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
							painterResource(Res.drawable.icon_notifications_active_24px), null,
							tint = MaterialTheme.colorScheme.primary
						)
						"bite" -> Icon(
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
						"follow_request_accepted" -> Icon(
							painterResource(Res.drawable.icon_person_add_24px), null,
							tint = MaterialTheme.colorScheme.primary
						)
					}

					Row(
						modifier = Modifier
							.clickable(onClick = {
								navHandler.navigate(ProfileRoute(suggestedAccount.id))
							})
					) {
						Avatar(suggestedAccount, smaller = true)
					}

					Text(
						text = translation(translationKey!!, replacementMap),
						modifier = Modifier.weight(1f),
						lineHeight = with(LocalDensity.current) { smallerAvatarSize.dp.toSp() },
						inlineContent = displayNameEmojiMapping
					)

					var timestampKey by remember { mutableStateOf(0) }
					key(timestampKey) {
						val timestamp = "${group.getCreatedAtTimestamp()?.toRelativeString(short = true)}"
						Text(
							text = timestamp,
							fontSize = 13.sp,
							maxLines = 1
						)
					}
					LaunchedEffect(Unit) {
						delay(10.seconds)
						timestampKey++
					}
				}
				//</editor-fold>

				//<editor-fold name="Non-mention notifications with a status included">
				if (relevantStatus != null) {
					Column(
						modifier = Modifier.padding(top = 10.dp)
					) {
						MiniStatus(relevantStatus)
					}
				}
				//</editor-fold>

				val actionsStartPadding = 24.dp + 10.dp

				//<editor-fold name="Follow request actions">
				if (group.type == "follow_request") {
					var actionsVisible by rememberSaveable { mutableStateOf(true) }

					fun respondToFollowRequest(type: String) = coroutineScope.launch {
						val res = when (type) {
							"accept" -> authorizeFollowRequest(suggestedAccount.id)
							"reject" -> rejectFollowRequest(suggestedAccount.id)
							"ignore" -> ignoreFollowRequest(suggestedAccount.id)
							else -> TODO() // TODO: make this an enum
						}

						if (res.error || res.response == null) {
							res.handleError(snackbarController)
							vibrateError(haptics)
							return@launch
						}

						onAction()
						actionsVisible = false
					}

					AnimatedVisibility(
						visible = actionsVisible,
						enter = expandVertically(),
						exit = shrinkVertically()
					) {
						FlowRow(
							modifier = Modifier.padding(top = 10.dp, start = actionsStartPadding),
							horizontalArrangement = Arrangement.spacedBy(10.dp)
						) {
							FilledTonalButton(onClick = { respondToFollowRequest("accept") }) {
								Icon(painterResource(Res.drawable.icon_check_24px), null)
								Text(stringResource(Res.string.accept))
							}
							OutlinedButton(onClick = { respondToFollowRequest("reject") }) {
								Icon(painterResource(Res.drawable.icon_close_24px), null)
								Text(stringResource(Res.string.reject))
							}
							if (getFeature("ignore_follow_request")) {
								OutlinedButton(onClick = { respondToFollowRequest("ignore") }) {
									Icon(painterResource(Res.drawable.icon_block_24px), null)
									Text(stringResource(Res.string.ignore))
								}
							}
						}
					}
				}
				//</editor-fold>

				// bites when iceshrimp implements this route
			}

			Divider()
		}
	}
}
