package site.remlit.snowdrop.component.navigationBar

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.painterResource
import site.remlit.snowdrop.model.NavigationBarOption
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_account_circle_24px
import snowdrop.shared.generated.resources.icon_explore_24px
import snowdrop.shared.generated.resources.icon_home_24px
import snowdrop.shared.generated.resources.icon_notifications_24px

@Composable
fun NavigationBarIcon(tab: NavigationBarOption) {
	val account by remember { getCurrentAccountObjectFlow() }
		.collectAsStateWithLifecycle(null)

	when (tab) {
		NavigationBarOption.Timeline -> Icon(painterResource(Res.drawable.icon_home_24px), null)

		NavigationBarOption.Notifications -> Icon(painterResource(Res.drawable.icon_notifications_24px), null)

		NavigationBarOption.Explore -> Icon(painterResource(Res.drawable.icon_explore_24px), null)

		NavigationBarOption.MyProfile -> {
			@Composable
			fun fallbackAvatarIcon() {
				Icon(painterResource(Res.drawable.icon_account_circle_24px), null)
			}

			if (account != null && account!!.avatar != null) {
				KamelImage(
					resource = {
						asyncPainterResource(
							account!!.avatarStatic ?: account!!.avatar!!
						)
					},
					contentDescription = account!!.avatarDescription,
					contentScale = ContentScale.Crop,
					onLoading = { fallbackAvatarIcon() },
					modifier = Modifier.clip(CircleShape)
						.height(24.dp)
						.width(24.dp)
				)
			} else fallbackAvatarIcon()
		}
	}
}
