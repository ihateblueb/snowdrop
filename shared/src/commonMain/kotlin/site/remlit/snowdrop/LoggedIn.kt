package site.remlit.snowdrop

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.russhwolf.settings.ExperimentalSettingsApi
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.painterResource
import site.remlit.snowdrop.util.getCurrentAccountId
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow
import site.remlit.snowdrop.util.updateCurrentAccountObject
import site.remlit.snowdrop.view.Explore
import site.remlit.snowdrop.view.Notifications
import site.remlit.snowdrop.view.Profile
import site.remlit.snowdrop.view.Timeline
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_account_circle_24px
import snowdrop.shared.generated.resources.icon_account_circle_filled_24px
import snowdrop.shared.generated.resources.icon_explore_24px
import snowdrop.shared.generated.resources.icon_explore_filled_24px
import snowdrop.shared.generated.resources.icon_home_24px
import snowdrop.shared.generated.resources.icon_home_filled_24px
import snowdrop.shared.generated.resources.icon_notifications_24px
import snowdrop.shared.generated.resources.icon_notifications_filled_24px

@Composable
@Preview
@OptIn(ExperimentalSettingsApi::class)
fun LoggedIn() {
	LaunchedEffect(Unit) {
		updateCurrentAccountObject()
	}

	val account by getCurrentAccountObjectFlow().collectAsStateWithLifecycle(null)

	var selection by remember { mutableStateOf(0) }

	@Composable
	fun fallbackAvatarIcon() {
		if (selection == 0) Icon(painterResource(Res.drawable.icon_account_circle_filled_24px), null)
		else Icon(painterResource(Res.drawable.icon_account_circle_24px), null)
	}

	MaterialTheme {

		Scaffold(
			bottomBar = {
				NavigationBar {
					NavigationBarItem(
						selected = (selection == 0),
						onClick = { selection = 0 },
						icon = {
							if (selection == 0) Icon(painterResource(Res.drawable.icon_home_filled_24px), null)
							else Icon(painterResource(Res.drawable.icon_home_24px), null)
						},
						label = { Text("Timeline") }
					)

					NavigationBarItem(
						selected = (selection == 1),
						onClick = { selection = 1 },
						icon = {
							if (selection == 1) Icon(painterResource(Res.drawable.icon_notifications_filled_24px), null)
							else Icon(painterResource(Res.drawable.icon_notifications_24px), null)
						},
						label = { Text("Notifications") }
					)

					NavigationBarItem(
						selected = (selection == 2),
						onClick = { selection = 2 },
						icon = {
							if (selection == 2) Icon(painterResource(Res.drawable.icon_explore_filled_24px), null)
							else Icon(painterResource(Res.drawable.icon_explore_24px), null)
						},
						label = { Text("Explore") }
					)

					NavigationBarItem(
						selected = (selection == 3),
						onClick = { selection = 3 },
						icon = {
							// this is slow
							if (account != null && account!!.avatarStatic != null) {
								KamelImage(
									{ asyncPainterResource(account!!.avatarStatic!!) },
									"Profile",
									onLoading = { fallbackAvatarIcon() },
									modifier = Modifier.clip(CircleShape)
										.height(24.dp)
										.width(24.dp)
								)
							} else fallbackAvatarIcon()
						},
						label = { Text("Profile") }
					)
				}
			}
		) { bottomPadding ->
			Column(
				modifier = Modifier.padding(bottom = bottomPadding.calculateBottomPadding())
			) {
				Column {
					when (selection) {
						0 -> Timeline()
						1 -> Notifications()
						2 -> Explore()
						3 -> Profile(account!!.id)

						else -> Text("Something went wrong...")
					}
				}
			}
		}

	}
}