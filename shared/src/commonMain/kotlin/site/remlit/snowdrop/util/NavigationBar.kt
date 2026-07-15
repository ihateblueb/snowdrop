package site.remlit.snowdrop.util

import androidx.navigation.NavController
import site.remlit.snowdrop.ExploreRoute
import site.remlit.snowdrop.MyProfileRoute
import site.remlit.snowdrop.NotificationsRoute
import site.remlit.snowdrop.TimelineRoute
import site.remlit.snowdrop.model.NavigationBarOption

fun navigationBarNavigate(tab: NavigationBarOption, navController: NavController) {
	when (tab) {
		NavigationBarOption.Timeline -> navController.navigate(TimelineRoute)
		NavigationBarOption.Notifications -> navController.navigate(NotificationsRoute)
		NavigationBarOption.Explore -> navController.navigate(ExploreRoute)
		NavigationBarOption.MyProfile -> navController.navigate(MyProfileRoute)
	}
}

fun String.mapToNavigationOptions(): List<NavigationBarOption> {
	val navOptions = mutableListOf<NavigationBarOption>()
	this.split(" ").forEach {
		safe { navOptions.add(NavigationBarOption.valueOf(it)) }
	}
	return navOptions
}
