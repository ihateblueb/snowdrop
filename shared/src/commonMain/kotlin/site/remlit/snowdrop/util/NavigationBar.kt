package site.remlit.snowdrop.util

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import site.remlit.snowdrop.ExploreRoute
import site.remlit.snowdrop.MyProfileRoute
import site.remlit.snowdrop.NotificationsRoute
import site.remlit.snowdrop.TimelineRoute
import site.remlit.snowdrop.model.NavigationBarOption
import kotlin.time.Duration.Companion.milliseconds

/**
 * Handles navigation for each tab to their designated route.
 *
 * @param tab Navigation bar tab type
 * @param navController Navigation controller to use to navigate
 *
 * @since 0.0.4-alpha
 * */
fun navigationBarNavigate(tab: NavigationBarOption, navController: NavController) {
	when (tab) {
		NavigationBarOption.Timeline -> navController.navigate(TimelineRoute)
		NavigationBarOption.Notifications -> navController.navigate(NotificationsRoute)
		NavigationBarOption.Explore -> navController.navigate(ExploreRoute)
		NavigationBarOption.MyProfile -> navController.navigate(MyProfileRoute)
	}
}

/**
 * Creates an interaction source for navigation bar items.
 * Handles onClick and onLongClick for the tabs.
 *
 * @param tab Navigation bar tab type
 *
 * @since 0.0.4-alpha
 * */
@Composable
fun navigationBarInteractionSource(
	tab: NavigationBarOption
): MutableInteractionSource {
	val navController = LocalNavController.current

	val haptics = LocalHapticFeedback.current
	val viewConfiguration = LocalViewConfiguration.current

	val interactionSource = remember { MutableInteractionSource() }

	LaunchedEffect(interactionSource) {
		var isLongPress = false

		interactionSource.interactions.collectLatest { interaction ->
			when (interaction) {
				is PressInteraction.Press -> {
					isLongPress = false
					delay(viewConfiguration.longPressTimeoutMillis.milliseconds)
					isLongPress = true

					// add long press options here
					when (tab) {
						NavigationBarOption.MyProfile -> {
							haptics.performHapticFeedback(HapticFeedbackType.LongPress)
							showAccountSwitcher = true
						}
						else -> {}
					}
				}

				is PressInteraction.Release -> {
					if (!isLongPress) {
						vibrateSoft(haptics)
						navigationBarNavigate(tab, navController)
					}
				}
			}
		}
	}

	return interactionSource
}

/**
 * Automatically and safely maps a string of a list of navigation bar options
 * into a list of navigation bar options.
 *
 * @return Parsed list of navigation bar options
 * @since 0.0.4-alpha
 * */
fun String.mapToNavigationOptions(): List<NavigationBarOption> {
	val navOptions = mutableListOf<NavigationBarOption>()
	this.split(" ").forEach {
		safe { navOptions.add(NavigationBarOption.valueOf(it)) }
	}
	return navOptions
}
