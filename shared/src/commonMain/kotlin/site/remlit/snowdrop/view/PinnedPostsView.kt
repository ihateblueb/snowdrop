package site.remlit.snowdrop.view

import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.api.accounts.getStatuses
import site.remlit.snowdrop.component.NavigationBackButton
import site.remlit.snowdrop.component.RefreshableTimeline
import site.remlit.snowdrop.component.Status
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.util.LocalNavController
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.pinned_posts

@Composable
fun PinnedPostsView(id: String) = ViewSurface {
	val navHandler = LocalNavController.current

	TopAppBar(
		navigationIcon = { NavigationBackButton() },
		title = {
			Text(stringResource(Res.string.pinned_posts))
		}
	)

	RefreshableTimeline(
		fetchMethod = { maxId, minId, sinceId -> getStatuses(userId = id, maxId = maxId, minId = minId, sinceId = sinceId, pinned = true) },
		timelineComponent = { item, onUpdate -> Status(item, onUpdate) }
	)
}
