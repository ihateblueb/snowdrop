package site.remlit.snowdrop.component.navigationBar

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.model.NavigationBarOption
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.explore
import snowdrop.shared.generated.resources.notifications
import snowdrop.shared.generated.resources.profile
import snowdrop.shared.generated.resources.timeline

@Composable
fun NavigationBarLabel(tab: NavigationBarOption): String =
	when (tab) {
		// stringResource over translation due to the lack of placeholders
		NavigationBarOption.Timeline  -> stringResource(Res.string.timeline)
		NavigationBarOption.Notifications -> stringResource(Res.string.notifications)
		NavigationBarOption.Explore -> stringResource(Res.string.explore)
		NavigationBarOption.MyProfile -> stringResource(Res.string.profile)
	}
