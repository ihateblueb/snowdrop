package site.remlit.snowdrop.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import site.remlit.snowdrop.StatusRoute
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.atRoute
import site.remlit.snowdrop.util.toRelativeString
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_globe_20px
import snowdrop.shared.generated.resources.icon_home_20px
import snowdrop.shared.generated.resources.icon_lock_20px
import snowdrop.shared.generated.resources.icon_mail_20px

@Composable
fun MiniStatus(status: Status) {
	val navHandler = LocalNavController.current

	Card(
		modifier = Modifier.fillMaxWidth()
			.clickable(
				onClick = {
					navHandler.navigate(StatusRoute(status.id))
				}
			)
	) {
		Column(modifier = Modifier.padding(10.dp)) {
			Row(
				horizontalArrangement = Arrangement.spacedBy(5.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Avatar(status.account, small = true)
				Text(
					status.account.displayName ?: status.account.username,
					fontWeight = FontWeight.Bold,
					maxLines = 1
				)

				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.End
				) {
					Row(
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.spacedBy(5.dp)
					) {
						Text("${status.getCreatedAtTimestamp()?.toRelativeString()}")
						Visibility(status.visibility)
					}
				}
			}

			if (status.content != null) {
				Column(
					modifier = Modifier.padding(top = 5.dp)
				) {
					HtmlContent(status.content, status.mentions, maxLines = 3)
				}
			}
		}
	}
}