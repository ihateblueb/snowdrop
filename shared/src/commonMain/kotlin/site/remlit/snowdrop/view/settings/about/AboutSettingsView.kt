package site.remlit.snowdrop.view.settings.about

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.AboutInstanceRoute
import site.remlit.snowdrop.AboutSnowdropRoute
import site.remlit.snowdrop.component.SettingsCard
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.util.LocalNavController
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.about
import snowdrop.shared.generated.resources.about_instance
import snowdrop.shared.generated.resources.about_snowdrop
import snowdrop.shared.generated.resources.icon_arrow_back_24
import snowdrop.shared.generated.resources.icon_info_24px

@Composable
fun AboutSettingsView() = ViewSurface {
	val navHandler = LocalNavController.current

	TopAppBar(
		navigationIcon = {
			IconButton(onClick = { navHandler.popBackStack() }) {
				Icon(painterResource(Res.drawable.icon_arrow_back_24), null)
			}
		},
		title = {
			Text(stringResource(Res.string.about))
		}
	)

	LazyColumn(
		modifier = Modifier.padding(horizontal = 10.dp)
	) {
		item {
			SettingsCard(
				position = 0, size = 2,
				icon = { color, modifier ->
					Icon(painterResource(Res.drawable.icon_info_24px), null,
						modifier = modifier, tint = color)
				},
				headlineContent = stringResource(Res.string.about_instance),
				onClick = { navHandler.navigate(AboutInstanceRoute) }
			)
		}
		item {
			SettingsCard(
				position = 1, size = 2,
				icon = { color, modifier ->
					Icon(painterResource(Res.drawable.icon_info_24px), null,
						modifier = modifier, tint = color)
				},
				headlineContent = stringResource(Res.string.about_snowdrop),
				onClick = { navHandler.navigate(AboutSnowdropRoute) }
			)
		}
	}
}
