package site.remlit.snowdrop.view.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.cache.fetchInstance
import site.remlit.snowdrop.util.getCurrentAccountHost
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.about_instance
import snowdrop.shared.generated.resources.icon_arrow_back_24

@Composable
fun AboutInstanceView() = ViewSurface {
	val navHandler = LocalNavController.current

	val instance by fetchInstance().collectAsState(null)

	TopAppBar(
		navigationIcon = {
			IconButton(onClick = { navHandler.popBackStack() }) {
				Icon(painterResource(Res.drawable.icon_arrow_back_24), null)
			}
		},
		title = {
			Text(stringResource(Res.string.about_instance))
		}
	)

	LazyColumn {
		if (instance == null) {

		} else {
			item {
				Column(
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.spacedBy(10.dp),
					modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 20.dp, bottom = 20.dp)
						.fillMaxWidth()
				) {
					Box(
						modifier = Modifier
					) {
						KamelImage(
							resource = { asyncPainterResource("https://${getCurrentAccountHost()}/favicon.ico") },
							contentDescription = "Instance favicon",
							contentScale = ContentScale.Fit,
							modifier = Modifier.height(48.dp)
								.width(48.dp),
						)
					}

					Text(
						instance!!.title.trim(),
						fontWeight = FontWeight.Medium,
						fontSize = 18.sp
					)

					Text(instance!!.description ?: "")

					Text(
						instance!!.version,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
			}

			if (!instance!!.rules.isEmpty()) {
				item {
					Text(
						"Rules",
						fontWeight = FontWeight.Medium,
						fontSize = 18.sp,
						modifier = Modifier.padding(15.dp)
					)
				}

				instance!!.rules.forEach {
					item {
						Column(
							modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
							verticalArrangement = Arrangement.spacedBy(5.dp)
						) {
							Text("${it.id}. ${it.text}")
							Text(it.hint, color = MaterialTheme.colorScheme.onSurfaceVariant)
						}
					}
				}
			}
		}
	}
}
