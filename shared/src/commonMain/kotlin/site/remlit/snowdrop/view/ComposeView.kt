package site.remlit.snowdrop.view

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.painterResource
import site.remlit.snowdrop.util.LocalNavController
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_outline_arrow_back_24

@Composable
fun ComposeView() {
	val navHandler = LocalNavController.current

	TopAppBar(
		navigationIcon = {
			IconButton(onClick = { navHandler.popBackStack() }) {
				Icon(painterResource(Res.drawable.icon_outline_arrow_back_24), null)
			}
		},
		title = {
			Text("Compose")
		}
	)
}