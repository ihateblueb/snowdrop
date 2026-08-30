package site.remlit.snowdrop.component

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.util.LocalNavController
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.back
import snowdrop.shared.generated.resources.close
import snowdrop.shared.generated.resources.icon_arrow_back_24
import snowdrop.shared.generated.resources.icon_close_24px

@Composable
fun NavigationBackButton(close: Boolean = false) {
	val navHandler = LocalNavController.current

	val __translation = stringResource(if (close) Res.string.close else Res.string.back)
	IconButton(
		onClick = { navHandler.popBackStack() },
		modifier = Modifier.semantics { contentDescription = __translation }
	) {
		Icon(painterResource(if (close) Res.drawable.icon_close_24px
			else Res.drawable.icon_arrow_back_24), null)
	}
}
