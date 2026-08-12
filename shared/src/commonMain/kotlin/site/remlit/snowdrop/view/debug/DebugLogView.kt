package site.remlit.snowdrop.view.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.model.log.Level
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.config.json
import site.remlit.snowdrop.util.log.logs
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_arrow_back_24
import snowdrop.shared.generated.resources.logs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugLogView() = ViewSurface {
	val navHandler = LocalNavController.current
	// TODO: update to LocalClipboard when this issue is resolved https://youtrack.jetbrains.com/issue/CMP-7624
	val clipboardManager = LocalClipboardManager.current

	TopAppBar(
		navigationIcon = {
			IconButton(onClick = { navHandler.popBackStack() }) {
				Icon(painterResource(Res.drawable.icon_arrow_back_24), null)
			}
		},
		title = {
			Text(stringResource(Res.string.logs))
		},
		actions = {
			Button(onClick = {
				clipboardManager.setText(AnnotatedString(
					json.encodeToString(logs.toList())
				))
			}) {
				Text("Copy")
			}
		}
	)

	LazyColumn(
		modifier = Modifier.padding(horizontal = 10.dp)
	) {
		items(items = logs) { log ->
			Column(
				modifier = Modifier.padding(5.dp)
			) {

				Row {
					Box(
						modifier = Modifier.clip(RoundedCornerShape(5.dp))
							.background(when (log.level) {
								Level.Debug -> Color.Cyan.copy(alpha = 0.25f)
								Level.Info -> Color.Blue.copy(alpha = 0.25f)
								Level.Warn -> Color.Yellow.copy(alpha = 0.25f)
								Level.Error -> Color.Red.copy(alpha = 0.25f)
							})
					) {
						when (log.level) {
							Level.Debug -> Text("Debug", color = Color.Cyan)
							Level.Info -> Text("Info", color = Color.Blue)
							Level.Warn -> Text("Warn", color = Color.Yellow)
							Level.Error -> Text("Error", color = Color.Red)
						}
					}

					Text(
						log.at.toString(),
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}

				val scrollState = rememberScrollState()
				Text(
					log.message,
					modifier = Modifier.heightIn(max = 50.dp)
						.verticalScroll(scrollState)
				)

				HorizontalDivider(modifier = Modifier.fillMaxSize())
			}
		}
	}
}
