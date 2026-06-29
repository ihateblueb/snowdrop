package site.remlit.snowdrop.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.api.notifications.getNotifications
import site.remlit.snowdrop.component.Notification
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.model.Notification
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.followers
import snowdrop.shared.generated.resources.no_notifications
import snowdrop.shared.generated.resources.notifications

@Composable
@Preview
fun NotificationsView() = ViewSurface {
	val timeline = remember { mutableStateListOf<Notification>() }
	var ready by remember { mutableStateOf(false) }

	LaunchedEffect(Unit) {
		val res = getNotifications()
		if (res.error) return@LaunchedEffect
		if (res.response == null) return@LaunchedEffect
		timeline.addAll(res.response)

		ready = true
	}

	TopAppBar(
		title = {
			Text(stringResource(Res.string.notifications))
		}
	)

	LazyColumn(
		modifier = Modifier
			.fillMaxSize()
	) {
		if (!ready) {
			item {
				Column(
					modifier = Modifier.fillMaxHeight().fillMaxWidth(),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center
				) {
					CircularProgressIndicator()
				}
			}
		} else if (timeline.isEmpty()) {
			item {
				Column(
					modifier = Modifier.fillMaxHeight()
						.fillMaxWidth(),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center
				) {
					Text(stringResource(Res.string.no_notifications))
				}
			}
		} else {
			timeline.forEach {
				item { Notification(it) }
			}
		}
	}
}
