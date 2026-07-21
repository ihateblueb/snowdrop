package site.remlit.snowdrop.util

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

val pageEnterTransition = slideInHorizontally(
	initialOffsetX = { it },
	animationSpec = tween(
		durationMillis = 200,
		easing = FastOutSlowInEasing
	)
)

val pageExitTransition = slideOutHorizontally(
	targetOffsetX = { it },
	animationSpec = tween(
		durationMillis = 200,
		easing = FastOutSlowInEasing
	)
)

/**
 * NavGraphBuilder composable with proper transitions preapplied.
 *
 * @param block Composable for the destination
 *
 * @see composable
 * @since 0.0.4-alpha
 * */
inline fun <reified T : Any> NavGraphBuilder.transitionedComposable(
	noinline block: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) = composable<T>(
	enterTransition = { pageEnterTransition },
	exitTransition = { ExitTransition.None },
	popEnterTransition = { EnterTransition.None },
	popExitTransition = { pageExitTransition },
	content = block
)
