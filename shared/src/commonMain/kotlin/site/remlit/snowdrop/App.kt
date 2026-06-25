package site.remlit.snowdrop

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import site.remlit.snowdrop.model.ui.Destination
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.atRoute
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
import site.remlit.snowdrop.view.StatusView
import site.remlit.snowdrop.view.TimelineView
import site.remlit.snowdrop.view.settings.SettingsView
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
object StartRoute : Destination(0)
@Serializable
object LoginRoute : Destination(1)
@Serializable
object TimelineRoute : Destination(2)
@Serializable
object NotificationsRoute : Destination(3)
@Serializable
object ExploreRoute : Destination(4)
@Serializable
object MyProfileRoute : Destination(5)
@Serializable
data class ProfileRoute(val id: String) : Destination(6)
@Serializable
data class StatusRoute(val id: String) : Destination(7)

@Serializable
object Settings : Destination(100)


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


	fun shouldHideBottomBar(): Boolean {
		return atRoute<ProfileRoute>(currentDest) || atRoute<Settings>(currentDest)
	}

	@Composable
	fun fallbackAvatarIcon() {
		if (currentDest != null && currentDest.hasRoute<MyProfileRoute>()) Icon(painterResource(Res.drawable.icon_account_circle_filled_24px), null)
		else Icon(painterResource(Res.drawable.icon_account_circle_24px), null)
	}

	CompositionLocalProvider(LocalNavController provides navController) {
		CompositionLocalProvider(LocalKamelConfig provides kamelConfig) {

			Scaffold(
				bottomBar = {
					if (loggedIn == true && !shouldHideBottomBar()) {
						NavigationBar {
							NavigationBarItem(
								selected = atRoute<TimelineRoute>(currentDest),
								onClick = {
									if (!atRoute<TimelineRoute>(currentDest)) {
										navController.navigate(TimelineRoute)
									}
							  	},
								icon = {
									if (atRoute<TimelineRoute>(currentDest)) Icon(painterResource(Res.drawable.icon_home_filled_24px), null)
									else Icon(painterResource(Res.drawable.icon_home_24px), null)
								},
								label = { Text("Timeline") }
							)

							NavigationBarItem(
								selected = atRoute<NotificationsRoute>(currentDest),
								onClick = { navController.navigate(NotificationsRoute) },
								icon = {
									if (atRoute<NotificationsRoute>(currentDest)) Icon(painterResource(Res.drawable.icon_notifications_filled_24px), null)
									else Icon(painterResource(Res.drawable.icon_notifications_24px), null)
								},
								label = { Text("Notifications") }
							)

							NavigationBarItem(
								selected = atRoute<ExploreRoute>(currentDest),
								onClick = { navController.navigate(ExploreRoute) },
								icon = {
									if (atRoute<ExploreRoute>(currentDest)) Icon(painterResource(Res.drawable.icon_explore_filled_24px), null)
									else Icon(painterResource(Res.drawable.icon_explore_24px), null)
								},
								label = { Text("Explore") }
							)

							NavigationBarItem(
								selected = atRoute<MyProfileRoute>(currentDest),
								onClick = { navController.navigate(MyProfileRoute) },
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
						startDestination = StartRoute,
						enterTransition = { EnterTransition.None },
						exitTransition = { ExitTransition.None },
						popEnterTransition = { EnterTransition.None },
						popExitTransition = { ExitTransition.None }
					) {
						composable<StartRoute> {
							StartView(
								navigateToLogin = { navController.navigate(LoginRoute) },
								navigateToTimeline = { navController.navigate(TimelineRoute) },
							)
						}

						composable<LoginRoute> {
							LoginView(
								navigateToTimeline = { navController.navigate(TimelineRoute) },
							)
						}
						composable<TimelineRoute> { TimelineView() }
						composable<NotificationsRoute> { NotificationsView() }
						composable<ExploreRoute> { ExploreView() }
						composable<MyProfileRoute> {
							if (account != null) ProfileView(account!!.id)
							else Text("Error")
						}

						composable<StatusRoute> {
							val args = it.toRoute<StatusRoute>()
							StatusView(args.id)
						}
						composable<ProfileRoute> {
							val args = it.toRoute<ProfileRoute>()
							ProfileView(args.id)
						}

						// Settings
						composable<Settings> { SettingsView() }
					}
				}
			}

		}
	}
}