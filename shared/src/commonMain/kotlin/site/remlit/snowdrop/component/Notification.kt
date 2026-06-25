package site.remlit.snowdrop.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import site.remlit.snowdrop.model.Notification
import site.remlit.snowdrop.util.toRelativeString
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_alternate_email_24px
import snowdrop.shared.generated.resources.icon_repeat_24px
import snowdrop.shared.generated.resources.icon_star_24px

@Composable
fun Notification(notification: Notification) {


	if (notification.type == "mention" && notification.status != null) {
		Status(notification.status)
	} else {
		Column {
			Column(
				modifier = Modifier.padding(15.dp)
			) {
				Row(
					horizontalArrangement = Arrangement.spacedBy(10.dp)
				) {
					when (notification.type) {
						"favourite" -> Icon(
							painterResource(Res.drawable.icon_star_24px), null,
							tint = MaterialTheme.colorScheme.primary
						)
						"reblog" -> Icon(
							painterResource(Res.drawable.icon_repeat_24px), null,
							tint = MaterialTheme.colorScheme.primary
						)
						"bite" -> Icon( // todo: tooth, and test bite notifs
							painterResource(Res.drawable.icon_alternate_email_24px), null,
							tint = MaterialTheme.colorScheme.primary
						)
					}

					Avatar(notification.account, small = true)

					Column(
						verticalArrangement = Arrangement.spacedBy(2.dp)
					) {
						when (notification.type) {
							"favourite", "reblog", "bite" -> Text(
								notification.account.displayName ?: notification.account.username,
								fontWeight = FontWeight.Bold
							)
						}

						when (notification.type) {
							"favourite" -> Text("liked your post")
							"reblog" -> Text("boosted your post")
							"bite" -> {
								if (notification.bite?.biteBack == true) Text("bit you back")
								else Text("bit you")
							}
						}
					}

					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.End
					) {
						Row {
							Text(
								"${notification.getCreatedAtTimestamp()?.toRelativeString()}",
								fontSize = 13.sp
							)
						}
					}
				}

				if (notification.status != null) {
					Column(
						modifier = Modifier.padding(top = 10.dp)
					) {
						MiniStatus(notification.status)
					}
				}
			}

			HorizontalDivider(
				thickness = 1.dp,
				color = MaterialTheme.colorScheme.surfaceContainer
			)
		}
	}
}