package site.remlit.snowdrop.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import site.remlit.snowdrop.ThreadRoute
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.extension.toRelativeString
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_warning_20px

@Composable
fun MiniStatus(
	status: Status,
	showContentEvenIfCw: Boolean = false
) {
	val navHandler = LocalNavController.current

	Column(
		modifier = Modifier.fillMaxWidth()
			.clip(RoundedCornerShape(10.dp))
			.border(1.dp, MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(10.dp))
			.clickable(onClick = {
				navHandler.navigate(ThreadRoute(status.id))
			})
	) {
		Column(modifier = Modifier.padding(10.dp)) {
			Row(
				horizontalArrangement = Arrangement.spacedBy(5.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Avatar(status.account!!, smaller = true)
				Text(
					status.account.displayName(),
					fontWeight = FontWeight.Bold,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					modifier = Modifier.weight(1f)
				)

				Row(
					horizontalArrangement = Arrangement.spacedBy(5.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						"${status.getCreatedAtTimestamp()?.toRelativeString()}",
						fontSize = 13.sp
					)
					Visibility(status.visibility!!)
				}
			}

			@Composable
			fun Content() {
				Row(
					modifier = Modifier.padding(top = 5.dp)
				) {
					HtmlContent(status.content ?: "", mentions = status.mentions, maxLines = 3)
				}
			}

			if (!status.spoilerText.isNullOrBlank()) {
				Row(
					modifier = Modifier.padding(top = 5.dp),
					horizontalArrangement = Arrangement.spacedBy(5.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					Icon(painterResource(Res.drawable.icon_warning_20px), null)
					Text(
						status.spoilerText,
						fontWeight = FontWeight.Medium
					)
				}

				if (showContentEvenIfCw && status.content != null) Content()
			} else if (status.content != null) Content()
		}
	}
}
