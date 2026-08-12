package site.remlit.snowdrop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.russhwolf.settings.ExperimentalSettingsApi
import io.kamel.image.config.LocalKamelConfig
import io.ktor.http.Url
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.component.AccountPickerList
import site.remlit.snowdrop.component.AppTheme
import site.remlit.snowdrop.component.navigationBar.NavigationBarIcon
import site.remlit.snowdrop.component.navigationBar.NavigationBarLabel
import site.remlit.snowdrop.util.ExternalUriHandler
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.LocalSnackbarController
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
import site.remlit.snowdrop.util.getNavigationBarOrder
import site.remlit.snowdrop.util.getNavigationBarOrderBlocking
import site.remlit.snowdrop.util.log.debug
import site.remlit.snowdrop.util.mapToNavigationOptions
import site.remlit.snowdrop.util.navigationBarInteractionSource
import site.remlit.snowdrop.util.safeReturnable
import site.remlit.snowdrop.util.showAccountSwitcher
import site.remlit.snowdrop.util.transitionedComposable
import site.remlit.snowdrop.view.*
import site.remlit.snowdrop.view.debug.DebugLogView
import site.remlit.snowdrop.view.debug.DebugView
import site.remlit.snowdrop.view.debug.DebugStorageView
import site.remlit.snowdrop.view.settings.*
import site.remlit.snowdrop.view.settings.about.AboutInstanceView
import site.remlit.snowdrop.view.settings.about.AboutSettingsView
import site.remlit.snowdrop.view.settings.about.AboutSnowdropView
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.add_account
import snowdrop.shared.generated.resources.icon_add_24px
import snowdrop.shared.generated.resources.icon_alternate_email_24px
import snowdrop.shared.generated.resources.icon_check_24px
import snowdrop.shared.generated.resources.icon_edit_square_24px
import snowdrop.shared.generated.resources.icon_list_arrow_24px
import snowdrop.shared.generated.resources.reorder
import snowdrop.shared.generated.resources.save


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
data class ExploreRoute(val immediateFocus: Boolean)
@Serializable
object MyProfileRoute
@Serializable
object EditProfileRoute
@Serializable
data class ProfileRoute(val id: String)
@Serializable
data class PinnedPostsRoute(val id: String)
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
data class StatusMediaAttachmentRoute(
	val id: String,
	val startingPosition: Int
)
@Serializable
data class ComposeRoute(
	val inReplyToId: String? = null,
	val editingId: String? = null,
	val cw: String = "",
	val content: String = "",
	val visibility: String? = null
)

@Serializable
object SettingsRoute
@Serializable
object AboutSettingsRoute
@Serializable
object AboutInstanceRoute
@Serializable
object AboutSnowdropRoute
@Serializable
object GeneralSettingsRoute
@Serializable
object AppearanceSettingsRoute
@Serializable
object WellbeingSettingsRoute

@Serializable
object DebugRoute
@Serializable
data class DebugStorageRoute(val storage: Int)
@Serializable
object DebugLogRoute


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

	val snackbarHostState = remember { SnackbarHostState() }


	val loggedIn by settings.getBooleanOrNullFlow("logged_in")
		.collectAsStateWithLifecycle(null)
	val account by getCurrentAccountObjectFlow()
		.collectAsStateWithLifecycle(null)


	DisposableEffect(Unit) {
		ExternalUriHandler.listener = { uri ->
			val parsed = safeReturnable { Url(uri) }
			debug { "(App) ExternalUriHandler received & parsed: $parsed" }

			if (parsed?.host == "oauth-callback" && parsed.parameters.contains("code"))
				blockingSettings.putString("oauth_callback", parsed.parameters["code"]!!)

			// if any other URIs need to be configured, they can be added here
		}

		onDispose { ExternalUriHandler.listener = null }
	}


	val shouldHideBottomBar = atRoute<ComposeRoute>(currentDest) ||
		atRoute<SettingsRoute>(currentDest) ||
		atRoute<AboutSettingsRoute>(currentDest) ||
		atRoute<AboutInstanceRoute>(currentDest) ||
		atRoute<AboutSnowdropRoute>(currentDest) ||
		atRoute<GeneralSettingsRoute>(currentDest) ||
		atRoute<AppearanceSettingsRoute>(currentDest) ||
		atRoute<WellbeingSettingsRoute>(currentDest) ||
		atRoute<DebugRoute>(currentDest) ||
		atRoute<DebugStorageRoute>(currentDest) ||
		atRoute<DebugLogRoute>(currentDest) ||
		atRoute<StatusMediaAttachmentRoute>(currentDest)

	val alwaysShowComposeButton by settings.getBooleanFlow("always_show_compose_button", false)
		.collectAsStateWithLifecycle(false)

	val shouldShowComposeFab = loggedIn == true &&
		(atRoute<TimelineRoute>(currentDest) ||
			atRoute<ProfileRoute>(currentDest)) &&
		(scrollingUpward || alwaysShowComposeButton)

	/*
	* UI Begins
	*/

	// this composable makes it easier to view and understand the composition
	// local providers which wrap the app
	@Composable
	fun Provided(content: @Composable () -> Unit) {
		CompositionLocalProvider(LocalNavController provides navController) {
			CompositionLocalProvider(LocalSnackbarController provides snackbarHostState) {
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
						visible = (loggedIn == true && !shouldHideBottomBar),
						enter = bottomNavEnterAnimation,
						exit = bottomNavExitAnimation,
					) {
						NavigationBar {
							val navigationBarOrder by remember { getNavigationBarOrder() }
								.collectAsStateWithLifecycle(defaultNavigationBarOrder)
							val showNavigationBarLabels by remember { settings.getBooleanFlow("show_navigation_bar_labels", true) }
								.collectAsStateWithLifecycle(true)

							key(navigationBarOrder) {
								navigationBarOrder.mapToNavigationOptions()
									.forEach { item ->
										NavigationBarItem(
											selected = atRoute(item.toRouteClass(), currentDest),
											onClick = { /* unimportant due to interaction source */ },
											interactionSource = navigationBarInteractionSource(item),
											icon = { NavigationBarIcon(item) },
											label = {
												if (showNavigationBarLabels)
													Text(
														NavigationBarLabel(item),
														overflow = TextOverflow.Ellipsis,
														maxLines = 1
													)
											}
										)
									}
							}
						}
					}
				},
				floatingActionButton = {
					AnimatedVisibility(
						visible = shouldShowComposeFab,
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
				snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
			) { paddingValues ->
				Box(
					modifier = Modifier.fillMaxSize()
						.padding(paddingValues)
						.consumeWindowInsets(WindowInsets(top = paddingValues.calculateTopPadding()))
				) {
					if (showAccountSwitcher) {
						ModalBottomSheet(
							onDismissRequest = { showAccountSwitcher = false }
						) {
							var reordering by remember { mutableStateOf(false) }

							AccountPickerList(
								modifier = Modifier.padding(horizontal = 15.dp),
								onSelect = { showAccountSwitcher = false },
								reordering = reordering
							)

							Row(
								modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 10.dp)
									.fillMaxWidth(),
								horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
							) {
								FilledTonalButton(
									onClick = {
										showAccountSwitcher = false
										addNewAccount(navController)
									},
									enabled = !reordering
								) {
									Icon(painterResource(Res.drawable.icon_add_24px), null)
									Spacer(Modifier.size(ButtonDefaults.IconSpacing))
									Text(stringResource(Res.string.add_account))
								}

								FilledTonalButton(
									onClick = { reordering = !reordering },
								) {
									Icon(
										if (reordering) painterResource(Res.drawable.icon_check_24px)
										else painterResource(Res.drawable.icon_list_arrow_24px),
										null
									)
									Spacer(Modifier.size(ButtonDefaults.IconSpacing))
									Text(
										if (reordering) stringResource(Res.string.save)
										else stringResource(Res.string.reorder)
									)
								}
							}
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

						composable<LoginRoute> { LoginView() }
						composable<TimelineRoute> { TimelineView() }
						composable<NotificationsRoute> { NotificationsView() }
						composable<ExploreRoute> {
							val args = it.toRoute<ExploreRoute>()
							ExploreView(args.immediateFocus)
						}
						composable<MyProfileRoute> {
							if (account != null) ProfileView(account!!.id)
							else Text("Error")
						}
						transitionedComposable<EditProfileRoute> { EditProfileView() }

						transitionedComposable<ThreadRoute> {
							val args = it.toRoute<ThreadRoute>()
							ThreadView(args.id)
						}
						transitionedComposable<StatusInteractionDetailRoute> {
							val args = it.toRoute<StatusInteractionDetailRoute>()
							StatusInteractionDetailView(args.id, InteractionViewType.valueOf(args.type))
						}
						transitionedComposable<StatusMediaAttachmentRoute> {
							val args = it.toRoute<StatusMediaAttachmentRoute>()
							StatusMediaAttachmentView(args.id, args.startingPosition)
						}
						transitionedComposable<ProfileRoute> {
							val args = it.toRoute<ProfileRoute>()
							ProfileView(args.id)
						}
						transitionedComposable<PinnedPostsRoute> {
							val args = it.toRoute<PinnedPostsRoute>()
							PinnedPostsView(args.id)
						}

						transitionedComposable<ComposeRoute> {
							val args = it.toRoute<ComposeRoute>()
							ComposeView(
								args.inReplyToId,
								args.editingId,
								args.cw,
								args.content,
								args.visibility
							)
						}

						// Settings
						transitionedComposable<SettingsRoute> { SettingsView() }
						transitionedComposable<AboutSettingsRoute> { AboutSettingsView() }
						transitionedComposable<AboutInstanceRoute> { AboutInstanceView() }
						transitionedComposable<AboutSnowdropRoute> { AboutSnowdropView() }
						transitionedComposable<GeneralSettingsRoute> { GeneralSettingsView() }
						transitionedComposable<AppearanceSettingsRoute> { AppearanceSettingsView() }
						transitionedComposable<WellbeingSettingsRoute> { WellbeingSettingsView() }

						// Debug
						transitionedComposable<DebugRoute> { DebugView() }
						transitionedComposable<DebugStorageRoute> {
							val args = it.toRoute<DebugStorageRoute>()
							DebugStorageView(args.storage)
						}
						transitionedComposable<DebugLogRoute> { DebugLogView() }
					}
				}
			}
		}
	}
}
