package site.remlit.snowdrop.model

import kotlinx.serialization.Serializable
import site.remlit.snowdrop.ExploreRoute
import site.remlit.snowdrop.MyProfileRoute
import site.remlit.snowdrop.NotificationsRoute
import site.remlit.snowdrop.TimelineRoute
import kotlin.reflect.KClass

/**
 * Enum representation of every option for navigation bar items.
 *
 * If you update this, you must update the following:
 * 	- [site.remlit.snowdrop.component.navigationBar.NavigationBarIcon]
 * 	- [site.remlit.snowdrop.component.navigationBar.NavigationBarLabel]
 * 	- [site.remlit.snowdrop.util.getBottomBarItemPageClass]
 * 	- [site.remlit.snowdrop.util.navigationBarNavigate]
 *
 * 	@since 0.0.4-alpha
 * */
@Serializable
enum class NavigationBarOption {
	/**
	 * @see [site.remlit.snowdrop.TimelineRoute]
	 * @since 0.0.4-alpha */
	Timeline,

	/**
	 * @see [site.remlit.snowdrop.NotificationsRoute]
	 * @since 0.0.4-alpha
	 * */
	Notifications,

	/**
	 * @see [site.remlit.snowdrop.ExploreRoute]
	 * @since 0.0.4-alpha */
	Explore,

	/**
	 * @see [site.remlit.snowdrop.MyProfileRoute]
	 * @since 0.0.4-alpha */
	MyProfile;

	fun toRouteClass(): KClass<out Any> = when (this) {
		Timeline -> TimelineRoute::class
		Notifications -> NotificationsRoute::class
		Explore -> ExploreRoute::class
		MyProfile -> MyProfileRoute::class
	}
}
