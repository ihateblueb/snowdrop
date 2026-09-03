package site.remlit.snowdrop.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.cache.getDrafts
import site.remlit.snowdrop.util.extension.toLocalizedString
import site.remlit.snowdrop.util.translation
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.delete
import snowdrop.shared.generated.resources.drafts
import snowdrop.shared.generated.resources.icon_arrow_back_24
import snowdrop.shared.generated.resources.icon_warning_24px
import snowdrop.shared.generated.resources.use
import kotlin.time.Instant

@Composable
fun DraftsView() = ViewSurface {
	val navHandler = LocalNavController.current

	TopAppBar(
		navigationIcon = {
			IconButton(onClick = { navHandler.popBackStack() }) {
				Icon(painterResource(Res.drawable.icon_arrow_back_24), null)
			}
		},
		title = { Text(translation(Res.string.drafts)) }
	)

	val drafts = getDrafts()

	LazyColumn {
		items(items = drafts, key = { it.id }) { draft ->

			Column(
				verticalArrangement = Arrangement.spacedBy(5.dp)
			) {
				Text(Instant.parse(draft.timestamp).toLocalizedString())

				if (draft.contentWarning != null) {
					Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
						Icon(painterResource(Res.drawable.icon_warning_24px), null)

						Text(
							text = draft.contentWarning,
							maxLines = 1
						)
					}
				} else {
					Text(
						text = draft.content ?: "",
						maxLines = 3
					)
				}

				Row {
					OutlinedButton(onClick = {}) {
						Text(stringResource(Res.string.delete))
					}
					FilledTonalButton(onClick = {}) {
						Text(stringResource(Res.string.use))
					}
				}
			}

		}
	}
}
