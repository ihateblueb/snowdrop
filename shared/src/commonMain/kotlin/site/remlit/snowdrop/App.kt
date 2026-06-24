package site.remlit.snowdrop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.russhwolf.settings.ExperimentalSettingsApi
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.kamel.image.config.LocalKamelConfig
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import site.remlit.snowdrop.model.User
import site.remlit.snowdrop.model.ui.Destination
import site.remlit.snowdrop.util.atRoute
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow
import site.remlit.snowdrop.util.kamelConfig
import site.remlit.snowdrop.util.safe
import site.remlit.snowdrop.util.settings
import site.remlit.snowdrop.util.setupAppSettings
import site.remlit.snowdrop.view.ExploreView
import site.remlit.snowdrop.view.LoginView
import site.remlit.snowdrop.view.NotificationsView
import site.remlit.snowdrop.view.ProfileView
import site.remlit.snowdrop.view.StartView
import site.remlit.snowdrop.view.TimelineView
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_account_circle_24px
import snowdrop.shared.generated.resources.icon_account_circle_filled_24px
import snowdrop.shared.generated.resources.icon_explore_24px
import snowdrop.shared.generated.resources.icon_explore_filled_24px
import snowdrop.shared.generated.resources.icon_home_24px
import snowdrop.shared.generated.resources.icon_home_filled_24px
import snowdrop.shared.generated.resources.icon_notifications_24px
import snowdrop.shared.generated.resources.icon_notifications_filled_24px

@Serializable
object Start : Destination(0)
@Serializable
object Login : Destination(1)
@Serializable
object Timeline : Destination(2)
@Serializable
object Notifications : Destination(3)
@Serializable
object Explore : Destination(4)
@Serializable
object MyProfile : Destination(5)
@Serializable
data class Profile(val id: String) : Destination(6)

@Composable
@Preview
@OptIn(ExperimentalSettingsApi::class)
fun App() = safe {
	setupAppSettings()

	val navController = rememberNavController()

	val navBackStackEntry by navController.currentBackStackEntryAsState()
	val currentDest = navBackStackEntry?.destination


	val loggedIn by settings.getBooleanOrNullFlow("logged_in")
		.collectAsStateWithLifecycle(null)
	val account by getCurrentAccountObjectFlow()
		.collectAsStateWithLifecycle(null)


	@Composable
	fun fallbackAvatarIcon() {
		if (currentDest != null && currentDest.hasRoute<MyProfile>()) Icon(painterResource(Res.drawable.icon_account_circle_filled_24px), null)
		else Icon(painterResource(Res.drawable.icon_account_circle_24px), null)
	}

	CompositionLocalProvider(LocalKamelConfig provides kamelConfig) {

		Scaffold(
			bottomBar = {
				if (loggedIn == true) {
					NavigationBar {
						NavigationBarItem(
							selected = atRoute<Timeline>(currentDest),
							onClick = { navController.navigate(Timeline) },
							icon = {
								if (atRoute<Timeline>(currentDest)) Icon(painterResource(Res.drawable.icon_home_filled_24px), null)
								else Icon(painterResource(Res.drawable.icon_home_24px), null)
							},
							label = { Text("Timeline") }
						)

						NavigationBarItem(
							selected = atRoute<Notifications>(currentDest),
							onClick = { navController.navigate(Notifications) },
							icon = {
								if (atRoute<Notifications>(currentDest)) Icon(painterResource(Res.drawable.icon_notifications_filled_24px), null)
								else Icon(painterResource(Res.drawable.icon_notifications_24px), null)
							},
							label = { Text("Notifications") }
						)

						NavigationBarItem(
							selected = atRoute<Explore>(currentDest),
							onClick = { navController.navigate(Explore) },
							icon = {
								if (atRoute<Explore>(currentDest)) Icon(painterResource(Res.drawable.icon_explore_filled_24px), null)
								else Icon(painterResource(Res.drawable.icon_explore_24px), null)
							},
							label = { Text("Explore") }
						)

						NavigationBarItem(
							selected = atRoute<MyProfile>(currentDest),
							onClick = { navController.navigate(MyProfile) },
							icon = {
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
			}
		) { bottomPadding ->
			Column(
				modifier = Modifier.padding(bottom = bottomPadding.calculateBottomPadding())
			) {
				NavHost(
					navController = navController,
					startDestination = Start
				) {
					composable<Start> {
						StartView(
							navigateToLogin = { navController.navigate(Login) },
							navigateToTimeline = { navController.navigate(Timeline) },
						)
					}

					composable<Login> {
						LoginView(
							navigateToTimeline = { navController.navigate(Timeline) },
						)
					}
					composable<Timeline> { TimelineView() }
					composable<Notifications> { NotificationsView() }
					composable<Explore> { ExploreView() }
					composable<MyProfile> {
						if (account != null) ProfileView(account!!.id)
						else Text("Error")
					}

					composable<Profile> {
						val args = it.toRoute<Profile>()
						ProfileView(args.id)
					}
				}
			}
		}

	}
}