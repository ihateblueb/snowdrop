package site.remlit.snowdrop.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.russhwolf.settings.ExperimentalSettingsApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.api.notifications.getNotifications
import site.remlit.snowdrop.component.Notification
import site.remlit.snowdrop.component.RefreshableTimeline
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.getFeature
import site.remlit.snowdrop.util.settings
import site.remlit.snowdrop.util.translation
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.bite
import snowdrop.shared.generated.resources.boost
import snowdrop.shared.generated.resources.follow
import snowdrop.shared.generated.resources.follow_request
import snowdrop.shared.generated.resources.icon_filter_alt_24px
import snowdrop.shared.generated.resources.icon_filter_alt_filled_24px
import snowdrop.shared.generated.resources.like
import snowdrop.shared.generated.resources.mention
import snowdrop.shared.generated.resources.notifications
import snowdrop.shared.generated.resources.poll
import snowdrop.shared.generated.resources.post
import snowdrop.shared.generated.resources.post_edit
import snowdrop.shared.generated.resources.reaction

@Composable
@OptIn(ExperimentalSettingsApi::class)
fun NotificationsView() = ViewSurface {
	var refreshKey by remember { mutableStateOf(0) }
	val showFilters by remember { settings.getBooleanFlow("show_filters", false) }
		.collectAsStateWithLifecycle(false)

	val mentions by remember { settings.getBooleanFlow("notifications_filter_mentions", true) }
		.collectAsStateWithLifecycle(true)
	val likes by remember { settings.getBooleanFlow("notifications_filter_likes", true) }
		.collectAsStateWithLifecycle(true)
	val boosts by remember { settings.getBooleanFlow("notifications_filter_boosts", true) }
		.collectAsStateWithLifecycle(true)
	val reactions by remember { settings.getBooleanFlow("notifications_filter_reactions", true) }
		.collectAsStateWithLifecycle(true)
	val edits by remember { settings.getBooleanFlow("notifications_filter_edits", true) }
		.collectAsStateWithLifecycle(true)
	val polls by remember { settings.getBooleanFlow("notifications_filter_polls", true) }
		.collectAsStateWithLifecycle(true)
	val posts by remember { settings.getBooleanFlow("notifications_filter_posts", true) }
		.collectAsStateWithLifecycle(true)
	val bites by remember { settings.getBooleanFlow("notifications_filter_bites", true) }
		.collectAsStateWithLifecycle(true)
	val follows by remember { settings.getBooleanFlow("notifications_filter_follows", true) }
		.collectAsStateWithLifecycle(true)
	val followRequests by remember { settings.getBooleanFlow("notifications_filter_follow_requests", true) }
		.collectAsStateWithLifecycle(true)

	LaunchedEffect(mentions, likes, boosts, reactions, edits, polls, posts, bites, follows, followRequests) {
		refreshKey++
	}

	fun getExcludedTypes(): List<String> {
		val excludedTypes = mutableListOf<String>()

		if (!mentions) excludedTypes.add("mention") else excludedTypes.remove("mention")
		if (!likes) excludedTypes.add("favourite") else excludedTypes.remove("favourite")
		if (!boosts) excludedTypes.add("reblog") else excludedTypes.remove("reblog")
		if (!reactions) excludedTypes.addAll(listOf("reaction", "pleroma:emoji_reaction")) else excludedTypes.removeAll(listOf("reaction", "pleroma:emoji_reaction"))
		if (!edits) excludedTypes.add("update") else excludedTypes.remove("update")
		if (!polls) excludedTypes.add("poll") else excludedTypes.remove("poll")
		if (!posts) excludedTypes.add("status") else excludedTypes.remove("status")
		if (!bites) excludedTypes.add("bite") else excludedTypes.remove("bite")
		if (!follows) excludedTypes.add("follow") else excludedTypes.remove("follow")
		if (!followRequests) excludedTypes.add("follow_request") else excludedTypes.remove("follow_request")

		return excludedTypes
	}

	TopAppBar(
		title = {
			Text(stringResource(Res.string.notifications))
		},
		actions = {
			IconButton(onClick = { blockingSettings.putBoolean("show_filters", !showFilters) }) {
				if (showFilters) Icon(painterResource(Res.drawable.icon_filter_alt_filled_24px), null)
				else Icon(painterResource(Res.drawable.icon_filter_alt_24px), null)
			}
		}
	)

	AnimatedVisibility(
		visible = showFilters,
		enter = expandVertically() + fadeIn(),
		exit = fadeOut() + shrinkVertically()
	) {
		LazyRow(
			contentPadding = PaddingValues(horizontal = 10.dp),
			horizontalArrangement = Arrangement.spacedBy(5.dp)
		) {
			item {
				FilterChip(
					selected = mentions,
					onClick = { blockingSettings.putBoolean("notifications_filter_mentions", !mentions) },
					label = { Text(translation(Res.string.mention)) }
				)
			}
			item {
				FilterChip(
					selected = likes,
					onClick = { blockingSettings.putBoolean("notifications_filter_likes", !likes) },
					label = { Text(translation(Res.string.like)) }
				)
			}
			item {
				FilterChip(
					selected = boosts,
					onClick = { blockingSettings.putBoolean("notifications_filter_boosts", !boosts) },
					label = { Text(translation(Res.string.boost)) }
				)
			}
			item {
				FilterChip(
					selected = reactions,
					onClick = { blockingSettings.putBoolean("notifications_filter_reactions", !reactions) },
					label = { Text(translation(Res.string.reaction)) }
				)
			}
			item {
				FilterChip(
					selected = follows,
					onClick = { blockingSettings.putBoolean("notifications_filter_follows", !follows) },
					label = { Text(translation(Res.string.follow)) }
				)
			}
			item {
				FilterChip(
					selected = followRequests,
					onClick = { blockingSettings.putBoolean("notifications_filter_follow_requests", !followRequests) },
					label = { Text(translation(Res.string.follow_request)) }
				)
			}
			item {
				FilterChip(
					selected = polls,
					onClick = { blockingSettings.putBoolean("notifications_filter_polls", !polls) },
					label = { Text(translation(Res.string.poll)) }
				)
			}
			item {
				FilterChip(
					selected = posts,
					onClick = { blockingSettings.putBoolean("notifications_filter_posts", !posts) },
					label = { Text(translation(Res.string.post)) }
				)
			}
			item {
				FilterChip(
					selected = edits,
					onClick = { blockingSettings.putBoolean("notifications_filter_edits", !edits) },
					label = { Text(translation(Res.string.post_edit)) }
				)
			}
			if (getFeature("biting"))
				item {
					FilterChip(
						selected = bites,
						onClick = { blockingSettings.putBoolean("notifications_filter_bites", !bites) },
						label = { Text(translation(Res.string.bite)) }
					)
				}
		}
	}

	RefreshableTimeline(
		fetchMethod = { maxId, minId, sinceId -> getNotifications(maxId = maxId, minId = minId, sinceId = sinceId, excludeTypes = getExcludedTypes()) },
		refreshKey = refreshKey,
		timelineComponent = { Notification(it) },
	)
}
