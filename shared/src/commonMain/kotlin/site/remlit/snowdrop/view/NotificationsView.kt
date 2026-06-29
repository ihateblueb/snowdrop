package site.remlit.snowdrop.view

import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.api.notifications.getNotifications
import site.remlit.snowdrop.component.Notification
import site.remlit.snowdrop.component.RefreshableTimeline
import site.remlit.snowdrop.component.ViewSurface
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.notifications

@Composable
@Preview
fun NotificationsView() = ViewSurface {
	TopAppBar(
		title = {
			Text(stringResource(Res.string.notifications))
		}
	)

	RefreshableTimeline(
		fetchMethod = { maxId, minId, sinceId -> getNotifications(maxId = maxId, minId = minId, sinceId = sinceId) },
		timelineComponent = { item, onUpdate -> Notification(item, onUpdate) },
	)
}
