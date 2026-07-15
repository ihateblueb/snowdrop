package site.remlit.snowdrop.util

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import kotlin.reflect.KClass

/**
 * Determine if currently at a location in navigation.
 *
 * @param klass One of the Routes (e.g. [site.remlit.snowdrop.StartRoute])
 * @param c Current navigation destination
 *
 * @return If provided destination is current location
 * @since 0.0.3-alpha
 * */
fun atRoute(
	klass: KClass<*>,
	c: NavDestination?
): Boolean {
	if (c == null) return false
	return c.hasRoute(klass)
}


/**
 * Determine if currently at a location in navigation.
 *
 * @param c Navigation destination, one of the Routes (e.g. [site.remlit.snowdrop.StartRoute])
 *
 * @return If provided destination is current location
 * @since 0.0.1-alpha
 * */
inline fun <reified T : Any> atRoute(
	c: NavDestination?
): Boolean = atRoute(T::class, c)
