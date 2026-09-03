package site.remlit.snowdrop.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import site.remlit.snowdrop.api.statuses.getHistory
import site.remlit.snowdrop.component.Divider
import site.remlit.snowdrop.component.MiniStatus
import site.remlit.snowdrop.component.NavigationBackButton
import site.remlit.snowdrop.component.RefreshableTimeline
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.LocalSnackbarController
import site.remlit.snowdrop.util.cache.fetchStatus
import site.remlit.snowdrop.util.extension.toLocalizedString
import site.remlit.snowdrop.util.translation
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.edit_history
import kotlin.time.Instant

@Composable
fun EditHistoryView(id: String) = ViewSurface {
	val snackbarController = LocalSnackbarController.current

	val mainStatus by remember { fetchStatus(id, snackbarController) }.collectAsStateWithLifecycle(null)

	TopAppBar(
		navigationIcon = { NavigationBackButton() },
		title = { Text(translation(Res.string.edit_history)) }
	)

	if (mainStatus != null)
		RefreshableTimeline(
			fetchMethod = { _, _, _ -> getHistory(id) },
			timelineComponent = { it, _ ->
				Column {
					Column(
						Modifier.padding(all = 10.dp),
						verticalArrangement = Arrangement.spacedBy(10.dp)
					) {
						Text(
							Instant.parse(it.createdAt).toLocalizedString(),
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)

						MiniStatus(
							Status(
								id = it.id,
								content = it.content,
								spoilerText = it.spoilerText,
								sensitive = it.sensitive,
								account = it.account,
								createdAt = it.createdAt,
								mediaAttachments = it.mediaAttachments,
								emojis = it.emojis,
								visibility = mainStatus!!.visibility
							),
							history = true
						)
					}

					Divider()
				}
			}
	)
}
