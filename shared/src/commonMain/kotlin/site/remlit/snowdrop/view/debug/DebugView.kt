package site.remlit.snowdrop.view.debug

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.DebugStorageRoute
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.bg
import site.remlit.snowdrop.util.bgIO
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.cache.clearCacheEntries
import site.remlit.snowdrop.util.determineFeatures
import site.remlit.snowdrop.util.resetFeatures
import site.remlit.snowdrop.util.toggleLoggedInState
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.account
import snowdrop.shared.generated.resources.cache
import snowdrop.shared.generated.resources.clear_cache
import snowdrop.shared.generated.resources.clear_settings
import snowdrop.shared.generated.resources.debug
import snowdrop.shared.generated.resources.icon_arrow_back_24
import snowdrop.shared.generated.resources.icon_chevron_right_24px
import snowdrop.shared.generated.resources.icon_refresh_24px
import snowdrop.shared.generated.resources.reset_feature_determinations
import snowdrop.shared.generated.resources.storage
import snowdrop.shared.generated.resources.toggle_logged_in_state

@Composable
fun DebugView() = ViewSurface {
	val navHandler = LocalNavController.current

	TopAppBar(
		navigationIcon = {
			IconButton(onClick = { navHandler.popBackStack() }) {
				Icon(painterResource(Res.drawable.icon_arrow_back_24), null)
			}
		},
		title = {
			Text(stringResource(Res.string.debug))
		}
	)

	LazyColumn(
		modifier = Modifier.padding(horizontal = 10.dp)
	) {

		item {
			Card {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.storage)) },
					trailingContent = {
						Icon(painterResource(Res.drawable.icon_chevron_right_24px), null)
					},
					modifier = Modifier.clickable {
						navHandler.navigate(DebugStorageRoute(0))
					}
				)
			}
		}
		item {
			Card {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.cache)) },
					trailingContent = {
						Icon(painterResource(Res.drawable.icon_chevron_right_24px), null)
					},
					modifier = Modifier.clickable {
						navHandler.navigate(DebugStorageRoute(1))
					}
				)
			}
		}
		item {
			Card {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.toggle_logged_in_state)) },
					modifier = Modifier.clickable { toggleLoggedInState() }
				)
			}
		}
		item {
			Card {
				ListItem(
					leadingContent = { Icon(painterResource(Res.drawable.icon_refresh_24px), null) },
					headlineContent = { Text(stringResource(Res.string.reset_feature_determinations)) },
					modifier = Modifier.clickable { resetFeatures(); bgIO { determineFeatures() } }
				)
			}
		}
		item {
			Card {
				ListItem(
					leadingContent = { Icon(painterResource(Res.drawable.icon_refresh_24px), null) },
					headlineContent = { Text(stringResource(Res.string.clear_cache)) },
					modifier = Modifier.clickable { bg { clearCacheEntries() } }
				)
			}
		}
		item {
			Card {
				ListItem(
					leadingContent = { Icon(painterResource(Res.drawable.icon_refresh_24px), null) },
					headlineContent = { Text(stringResource(Res.string.clear_settings)) },
					modifier = Modifier.clickable { bg { blockingSettings.clear() } }
				)
			}
		}

	}
}
