package site.remlit.snowdrop.view.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.russhwolf.settings.ExperimentalSettingsApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.settings
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.hide_follow_counters
import snowdrop.shared.generated.resources.hide_interaction_counters
import snowdrop.shared.generated.resources.icon_arrow_back_24
import snowdrop.shared.generated.resources.wellbeing

@OptIn(ExperimentalSettingsApi::class)
@Composable
fun WellbeingSettingsView() = ViewSurface {
	val navHandler = LocalNavController.current

	TopAppBar(
		navigationIcon = {
			IconButton(onClick = { navHandler.popBackStack() }) {
				Icon(painterResource(Res.drawable.icon_arrow_back_24), null)
			}
		},
		title = {
			Text(stringResource(Res.string.wellbeing))
		}
	)

	LazyColumn(
		modifier = Modifier.padding(horizontal = 10.dp)
	) {
		item {
			val hideInteractionCounters by settings.getBooleanFlow("hide_interaction_counters", false)
				.collectAsStateWithLifecycle(false)

			Card {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.hide_interaction_counters)) },
					trailingContent = {
						Switch(
							hideInteractionCounters,
							onCheckedChange = { blockingSettings.putBoolean("hide_interaction_counters", it) }
						)
					}
				)
			}
		}
		item {
			val hideFollowCounters by settings.getBooleanFlow("hide_follow_counters", false)
				.collectAsStateWithLifecycle(false)

			Card {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.hide_follow_counters)) },
					trailingContent = {
						Switch(
							hideFollowCounters,
							onCheckedChange = { blockingSettings.putBoolean("hide_follow_counters", it) }
						)
					}
				)
			}
		}
	}
}
