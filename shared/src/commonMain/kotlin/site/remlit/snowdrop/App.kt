package site.remlit.snowdrop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import co.touchlab.kermit.Logger
import com.russhwolf.settings.ExperimentalSettingsApi
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.kamel.image.config.LocalKamelConfig
import io.ktor.http.Url
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import site.remlit.snowdrop.component.AppTheme
import site.remlit.snowdrop.model.ui.Destination
import site.remlit.snowdrop.util.ExternalUriHandler
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.atRoute
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow
import site.remlit.snowdrop.util.config.kamelConfig
import site.remlit.snowdrop.util.safe
import site.remlit.snowdrop.util.scrollingUpward
import site.remlit.snowdrop.util.settings
import site.remlit.snowdrop.util.setupAppSettings
import site.remlit.snowdrop.util.cache.setupCache
import site.remlit.snowdrop.util.safeReturnable
import site.remlit.snowdrop.view.*
import site.remlit.snowdrop.view.settings.*
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_account_circle_24px
import snowdrop.shared.generated.resources.icon_account_circle_filled_24px
import snowdrop.shared.generated.resources.icon_alternate_email_24px
import snowdrop.shared.generated.resources.icon_edit_square_24px
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
data class ThreadRoute(val id: String) : Destination(7)
@Serializable
object ComposeRoute : Destination(8)

@Serializable
object SettingsRoute : Destination(100)
@Serializable
data class SettingsDebugStorageRoute(val storage: Int) : Destination(101)


@Composable
@Preview
@OptIn(ExperimentalSettingsApi::class)
fun App() = safe {
	setupAppSettings()
	setupCache()

	/*
	* Variables & Handlers for Whole App Stuff
	*/

	val navController = rememberNavController()

	val navBackStackEntry by navController.currentBackStackEntryAsState()
	val currentDest = navBackStackEntry?.destination


	val loggedIn by settings.getBooleanOrNullFlow("logged_in")
		.collectAsStateWithLifecycle(null)
	val account by getCurrentAccountObjectFlow()
		.collectAsStateWithLifecycle(null)


	DisposableEffect(Unit) {
		ExternalUriHandler.listener = { uri ->
			val parsed = safeReturnable { Url(uri) }
			Logger.d { "URI received & parsed: $parsed" }

			if (parsed?.host == "oauth-callback" && parsed.parameters.contains("code"))
				blockingSettings.putString("oauth_callback", parsed.parameters["code"]!!)

			// if any other URIs need to be configured, they can be added here
		}

		onDispose { ExternalUriHandler.listener = null }
	}


	fun shouldHideBottomBar(): Boolean =
		atRoute<ProfileRoute>(currentDest) ||
			atRoute<ComposeRoute>(currentDest) ||
			atRoute<SettingsRoute>(currentDest) ||
			atRoute<SettingsDebugStorageRoute>(currentDest)

	fun shouldShowComposeFab(): Boolean =
		loggedIn == true &&
			(atRoute<TimelineRoute>(currentDest) ||
				atRoute<ProfileRoute>(currentDest)) &&
			scrollingUpward

	/*
	* UI Begins
	*/

	@Composable
	fun fallbackAvatarIcon() {
		if (currentDest != null && currentDest.hasRoute<MyProfileRoute>()) Icon(
			painterResource(Res.drawable.icon_account_circle_filled_24px),
			null
		)
		else Icon(painterResource(Res.drawable.icon_account_circle_24px), null)
	}

	AppTheme {

		CompositionLocalProvider(LocalNavController provides navController) {
			CompositionLocalProvider(LocalKamelConfig provides kamelConfig) {

				Scaffold(
					bottomBar = {
						if (loggedIn == true && !shouldHideBottomBar()) {
							NavigationBar {
								NavigationBarItem(
									selected = atRoute<TimelineRoute>(currentDest),
									onClick = {
										if (!atRoute<TimelineRoute>(currentDest))
											navController.navigate(TimelineRoute)
									},
									icon = {
										if (atRoute<TimelineRoute>(currentDest)) Icon(
											painterResource(Res.drawable.icon_home_filled_24px),
											null
										)
										else Icon(painterResource(Res.drawable.icon_home_24px), null)
									},
									label = { Text("Timeline") }
								)

								NavigationBarItem(
									selected = atRoute<NotificationsRoute>(currentDest),
									onClick = { navController.navigate(NotificationsRoute) },
									icon = {
										if (atRoute<NotificationsRoute>(currentDest)) Icon(
											painterResource(Res.drawable.icon_notifications_filled_24px),
											null
										)
										else Icon(painterResource(Res.drawable.icon_notifications_24px), null)
									},
									label = { Text("Notifications") }
								)

								NavigationBarItem(
									selected = atRoute<ExploreRoute>(currentDest),
									onClick = { navController.navigate(ExploreRoute) },
									icon = {
										if (atRoute<ExploreRoute>(currentDest)) Icon(
											painterResource(Res.drawable.icon_explore_filled_24px),
											null
										)
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
					},
					floatingActionButton = {
						AnimatedVisibility(
							visible = shouldShowComposeFab(),
							enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
							exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
						) {
							FloatingActionButton(
								onClick = { navController.navigate(ComposeRoute) }
							) {
								if (atRoute<ProfileRoute>(currentDest)) Icon(painterResource(Res.drawable.icon_alternate_email_24px), null)
								else Icon(painterResource(Res.drawable.icon_edit_square_24px), null)
							}
						}
					},
					floatingActionButtonPosition = FabPosition.End
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

							composable<ThreadRoute> {
								val args = it.toRoute<ThreadRoute>()
								ThreadView(args.id)
							}
							composable<ProfileRoute> {
								val args = it.toRoute<ProfileRoute>()
								ProfileView(args.id)
							}

							composable<ComposeRoute> { ComposeView() }

							// Settings
							composable<SettingsRoute> { SettingsView() }
							composable<SettingsDebugStorageRoute> {
								val args = it.toRoute<SettingsDebugStorageRoute>()
								SettingsDebugStorageView(args.storage)
							}
						}
					}
				}

			}
		}

	}
}
