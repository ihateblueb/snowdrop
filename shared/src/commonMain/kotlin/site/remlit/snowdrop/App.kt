package site.remlit.snowdrop

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import co.touchlab.kermit.Logger
import com.russhwolf.settings.ExperimentalSettingsApi
import io.kamel.image.config.LocalKamelConfig
import io.ktor.http.Url
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.component.AppTheme
import site.remlit.snowdrop.component.Avatar
import site.remlit.snowdrop.component.navigationBar.NavigationBarIcon
import site.remlit.snowdrop.component.navigationBar.NavigationBarLabel
import site.remlit.snowdrop.util.ExternalUriHandler
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.SnackbarController
import site.remlit.snowdrop.util.addNewAccount
import site.remlit.snowdrop.util.atRoute
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.navigationBarNavigate
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow
import site.remlit.snowdrop.util.safe
import site.remlit.snowdrop.util.scrollingUpward
import site.remlit.snowdrop.util.settings
import site.remlit.snowdrop.util.setupAppSettings
import site.remlit.snowdrop.util.cache.setupCache
import site.remlit.snowdrop.util.config.kamelConfig
import site.remlit.snowdrop.util.defaultNavigationBarOrder
import site.remlit.snowdrop.util.getAccountHost
import site.remlit.snowdrop.util.getAccountObjectFlow
import site.remlit.snowdrop.util.getAccounts
import site.remlit.snowdrop.util.getNavigationBarOrder
import site.remlit.snowdrop.util.getNavigationBarOrderBlocking
import site.remlit.snowdrop.util.getCurrentAccountId
import site.remlit.snowdrop.util.mapToNavigationOptions
import site.remlit.snowdrop.util.safeReturnable
import site.remlit.snowdrop.util.showAccountSwitcher
import site.remlit.snowdrop.util.switchAccount
import site.remlit.snowdrop.view.*
import site.remlit.snowdrop.view.debug.DebugView
import site.remlit.snowdrop.view.debug.DebugStorageView
import site.remlit.snowdrop.view.settings.*
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.add_account
import snowdrop.shared.generated.resources.icon_add_24px
import snowdrop.shared.generated.resources.icon_alternate_email_24px
import snowdrop.shared.generated.resources.icon_edit_square_24px
import kotlin.time.Duration.Companion.milliseconds


/*
* NOTE: Only primitive types are allowed in the route data class values.
* */
@Serializable
object StartRoute
@Serializable
object LoginRoute
@Serializable
object TimelineRoute
@Serializable
object NotificationsRoute
@Serializable
object ExploreRoute
@Serializable
object MyProfileRoute
@Serializable
object EditProfileRoute
@Serializable
data class ProfileRoute(val id: String)
@Serializable
data class ThreadRoute(val id: String)
/**
 * @param type [InteractionViewType] as a string
 * */
@Serializable
data class StatusInteractionDetailRoute(
	val id: String,
	val type: String
)
@Serializable
data class ComposeRoute(
	val inReplyToId: String? = null,
	val cw: String = "",
	val content: String = "",
	val visibility: String? = null
)

@Serializable
object SettingsRoute
@Serializable
object AboutInstanceRoute
@Serializable
object AboutSnowdropRoute

@Serializable
object DebugRoute
@Serializable
data class DebugStorageRoute(val storage: Int)


val bottomNavEnterAnimation = fadeIn() + slideInVertically(initialOffsetY = { it })
val bottomNavExitAnimation = slideOutVertically(targetOffsetY = { it }) + fadeOut()


@Composable
@OptIn(ExperimentalSettingsApi::class, ExperimentalMaterial3Api::class)
fun App() = safe {
	setupAppSettings()
	setupCache()

	/*
	* Variables & Handlers for Whole App Stuff
	*/

	val navController = rememberNavController()

	val navBackStackEntry by navController.currentBackStackEntryAsState()
	val currentDest = navBackStackEntry?.destination

	val haptics = LocalHapticFeedback.current
	val viewConfiguration = LocalViewConfiguration.current
	val accountSwitcherInteractionSource = remember { MutableInteractionSource() }

	LaunchedEffect(accountSwitcherInteractionSource) {
		var isLongPress = false

		accountSwitcherInteractionSource.interactions.collectLatest { interaction ->
			when (interaction) {
				is PressInteraction.Press -> {
					isLongPress = false
					delay(viewConfiguration.longPressTimeoutMillis.milliseconds)
					isLongPress = true
					haptics.performHapticFeedback(HapticFeedbackType.LongPress)
					showAccountSwitcher = true
				}

				is PressInteraction.Release -> {
					if (!isLongPress) navController.navigate(MyProfileRoute)
				}
			}
		}
	}

	val snackbarHostState = remember { SnackbarHostState() }


	val loggedIn by settings.getBooleanOrNullFlow("logged_in")
		.collectAsStateWithLifecycle(null)
	val account by getCurrentAccountObjectFlow()
		.collectAsStateWithLifecycle(null)

	val swappedMiddleNavs by settings.getBooleanFlow("swapped_middle_navs", false)
		.collectAsStateWithLifecycle(false)


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
		atRoute<ComposeRoute>(currentDest) ||
			atRoute<SettingsRoute>(currentDest) ||
			atRoute<DebugRoute>(currentDest) ||
			atRoute<DebugStorageRoute>(currentDest)

	fun shouldShowComposeFab(): Boolean =
		loggedIn == true &&
			(atRoute<TimelineRoute>(currentDest) ||
				atRoute<ProfileRoute>(currentDest)) &&
			scrollingUpward

	/*
	* UI Begins
	*/

	// this composable makes it easier to view and understand the composition
	// local providers which wrap the app
	@Composable
	fun Provided(content: @Composable () -> Unit) {
		CompositionLocalProvider(LocalNavController provides navController) {
			CompositionLocalProvider(SnackbarController provides snackbarHostState) {
				CompositionLocalProvider(LocalKamelConfig provides kamelConfig) {
					content()
				}
			}
		}
	}

	// app really starts here
	AppTheme {
		Provided {

			Scaffold(
				bottomBar = {
					AnimatedVisibility(
						visible = (loggedIn == true && !shouldHideBottomBar()),
						enter = bottomNavEnterAnimation,
						exit = bottomNavExitAnimation,
					) {
						NavigationBar {
							val navigationBarOrder by remember { getNavigationBarOrder() }.collectAsStateWithLifecycle(defaultNavigationBarOrder)

							navigationBarOrder.mapToNavigationOptions()
								.forEach { item ->
									NavigationBarItem(
										selected = atRoute(item.toRouteClass(), currentDest),
										onClick = { navigationBarNavigate(item, navController) },
										icon = { NavigationBarIcon(item) },
										label = { Text(NavigationBarLabel(item)) }
									)
								}
						}
					}
				},
				floatingActionButton = {
					AnimatedVisibility(
						visible = shouldShowComposeFab(),
						enter = bottomNavEnterAnimation,
						exit = bottomNavExitAnimation,
					) {
						FloatingActionButton(
							onClick = { navController.navigate(ComposeRoute()) }
						) {
							if (atRoute<ProfileRoute>(currentDest)) Icon(painterResource(Res.drawable.icon_alternate_email_24px), null)
							else Icon(painterResource(Res.drawable.icon_edit_square_24px), null)
						}
					}
				},
				floatingActionButtonPosition = FabPosition.End,
				snackbarHost = {
					SnackbarHost(hostState = snackbarHostState)
				}
			) { bottomPadding ->
				Column(
					modifier = Modifier.padding(bottom = bottomPadding.calculateBottomPadding())
				) {
					if (showAccountSwitcher)
						ModalBottomSheet(
							onDismissRequest = { showAccountSwitcher = false }
						) {
							// todo: redesign this. cards look bad!
							getAccounts().forEach { it ->
								val account by getAccountObjectFlow(it)
									.collectAsStateWithLifecycle(null)

								if (account != null) {
									Card(
										modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
											.clip(RoundedCornerShape(10.dp))
											.fillMaxWidth()
											.clickable {
												if (it != getCurrentAccountId())
													switchAccount(it, navController)
											},
										colors = CardDefaults.cardColors(
											containerColor = if (getCurrentAccountId() == it)
												MaterialTheme.colorScheme.secondaryContainer
											else MaterialTheme.colorScheme.surfaceContainerLow,
											contentColor = if (getCurrentAccountId() == it)
												MaterialTheme.colorScheme.onPrimaryContainer
											else MaterialTheme.colorScheme.onSurface,
										)
									) {
										Row(
											modifier = Modifier.padding(10.dp),
											horizontalArrangement = Arrangement.spacedBy(10.dp),
											verticalAlignment = Alignment.CenterVertically
										) {
											Avatar(account!!)

											Column {
												Text(
													account!!.displayName(),
													fontWeight = FontWeight.Medium
												)
												Text("@${account!!.username}@${getAccountHost(it)}")
											}
										}
									}
								}
							}

							TextButton(
								modifier = Modifier.padding(all = 10.dp).fillMaxWidth(),
								onClick = {
									showAccountSwitcher = false
									addNewAccount(navController)
								}
							) {
								Icon(painterResource(Res.drawable.icon_add_24px), null)
								Text(stringResource(Res.string.add_account))
							}
						}

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
								navigateToFirstPage = {
									navigationBarNavigate(
										getNavigationBarOrderBlocking().mapToNavigationOptions().first(),
										navController
									)
								},
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
						composable<EditProfileRoute> { EditProfileView() }

						// todo: replace these animations and make them better
						composable<ThreadRoute>(
							enterTransition = { slideIntoContainer(
								AnimatedContentTransitionScope.SlideDirection.Start, tween(
									250
								)
							) },
							exitTransition = { slideOutOfContainer(
								AnimatedContentTransitionScope.SlideDirection.End, tween(
									200
								)
							) }
						) {
							val args = it.toRoute<ThreadRoute>()
							ThreadView(args.id)
						}
						composable<StatusInteractionDetailRoute> {
							val args = it.toRoute<StatusInteractionDetailRoute>()
							StatusInteractionDetailView(args.id, InteractionViewType.valueOf(args.type))
						}
						composable<ProfileRoute>(
							enterTransition = { slideIntoContainer(
								AnimatedContentTransitionScope.SlideDirection.Start, tween(
									250
								)
							) },
							exitTransition = { slideOutOfContainer(
								AnimatedContentTransitionScope.SlideDirection.End, tween(
									200
								)
							) }
						) {
							val args = it.toRoute<ProfileRoute>()
							ProfileView(args.id)
						}

						composable<ComposeRoute>(
							enterTransition = { slideIntoContainer(
								AnimatedContentTransitionScope.SlideDirection.Start, tween(
									250
								)
							) },
							exitTransition = { slideOutOfContainer(
								AnimatedContentTransitionScope.SlideDirection.End, tween(
									200
								)
							) }
						) {
							val args = it.toRoute<ComposeRoute>()
							ComposeView(
								args.inReplyToId,
								args.cw,
								args.content,
								args.visibility
							)
						}

						// Settings
						composable<SettingsRoute>(
							enterTransition = { slideIntoContainer(
								AnimatedContentTransitionScope.SlideDirection.Start, tween(
									250
								)
							) },
							exitTransition = { slideOutOfContainer(
								AnimatedContentTransitionScope.SlideDirection.End, tween(
									200
								)
							) }
						) { SettingsView() }
						composable<AboutInstanceRoute>(
							enterTransition = { slideIntoContainer(
								AnimatedContentTransitionScope.SlideDirection.Start, tween(
									250
								)
							) },
							exitTransition = { slideOutOfContainer(
								AnimatedContentTransitionScope.SlideDirection.End, tween(
									200
								)
							) }
						) { AboutInstanceView() }
						composable<AboutSnowdropRoute>(
							enterTransition = { slideIntoContainer(
								AnimatedContentTransitionScope.SlideDirection.Start, tween(
									250
								)
							) },
							exitTransition = { slideOutOfContainer(
								AnimatedContentTransitionScope.SlideDirection.End, tween(
									200
								)
							) }
						) { AboutSnowdropView() }

						// Debug
						composable<DebugRoute> { DebugView() }
						composable<DebugStorageRoute> {
							val args = it.toRoute<DebugStorageRoute>()
							DebugStorageView(args.storage)
						}
					}
				}
			}

		}
	}
}
