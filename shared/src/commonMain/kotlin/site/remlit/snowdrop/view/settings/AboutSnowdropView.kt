package site.remlit.snowdrop.view.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.DebugRoute
import site.remlit.snowdrop.GradleVariables
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.navigationBarInteractionSource
import site.remlit.snowdrop.util.translation
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.about_snowdrop
import snowdrop.shared.generated.resources.icon_arrow_back_24
import snowdrop.shared.generated.resources.icon_bug_report_24px
import snowdrop.shared.generated.resources.icon_code_24px
import snowdrop.shared.generated.resources.icon_favorite_24px
import snowdrop.shared.generated.resources.icon_snowdrop_36
import snowdrop.shared.generated.resources.report_bug
import snowdrop.shared.generated.resources.support_development
import snowdrop.shared.generated.resources.view_source

@Composable
fun AboutSnowdropView() = ViewSurface {
	val navHandler = LocalNavController.current

	var versionClicks by remember { mutableStateOf(0) }
	LaunchedEffect(versionClicks) {
		if (versionClicks == 5) {
			navHandler.navigate(DebugRoute)
			versionClicks = 0
		}
	}

	TopAppBar(
		navigationIcon = {
			IconButton(onClick = { navHandler.popBackStack() }) {
				Icon(painterResource(Res.drawable.icon_arrow_back_24), null)
			}
		},
		title = {
			Text(stringResource(Res.string.about_snowdrop))
		}
	)


	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(10.dp),
		modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 20.dp, bottom = 20.dp)
			.fillMaxWidth()
	) {
		Icon(
			painterResource(Res.drawable.icon_snowdrop_36),
			"Snowdrop icon",
			tint = MaterialTheme.colorScheme.primary
		)

		Text(
			"Snowdrop",
			fontWeight = FontWeight.Bold,
			fontSize = 20.sp,
			modifier = Modifier.padding(top = 5.dp)
		)

		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier.clickable(
				interactionSource = MutableInteractionSource(),
				indication = null,
				onClick = { versionClicks++ }
			)
		) {
			Text(
				"${GradleVariables.version} (${GradleVariables.gitCommit}@${GradleVariables.gitBranch})",
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				fontFamily = FontFamily.Monospace
			)
		}

		FlowRow(
			modifier = Modifier.padding(top = 20.dp),
			horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)
		) {
			val uriHandler = LocalUriHandler.current

			OutlinedButton(onClick = { uriHandler.openUri("https://github.com/ihateblueb/snowdrop/issues/new") }) {
				Icon(painterResource(Res.drawable.icon_bug_report_24px), null)
				Spacer(Modifier.size(ButtonDefaults.IconSpacing))
				Text(translation(Res.string.report_bug))
			}

			OutlinedButton(onClick = { uriHandler.openUri("https://github.com/ihateblueb/snowdrop") }) {
				Icon(painterResource(Res.drawable.icon_code_24px), null)
				Spacer(Modifier.size(ButtonDefaults.IconSpacing))
				Text(translation(Res.string.view_source))
			}

			OutlinedButton(onClick = { uriHandler.openUri("https://github.com/sponsors/ihateblueb") }) {
				Icon(painterResource(Res.drawable.icon_favorite_24px), null)
				Spacer(Modifier.size(ButtonDefaults.IconSpacing))
				Text(translation(Res.string.support_development))
			}
		}
	}
}
