package site.remlit.snowdrop.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toBlockingSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import site.remlit.snowdrop.StartRoute
import site.remlit.snowdrop.api.verifyCredentials
import site.remlit.snowdrop.model.Account
import site.remlit.snowdrop.model.NavigationBarOption
import site.remlit.snowdrop.util.cache.getCacheEntry
import site.remlit.snowdrop.util.cache.putCacheEntry

@OptIn(ExperimentalSettingsApi::class)
expect val settings: FlowSettings

@OptIn(ExperimentalSettingsApi::class)
val blockingSettings = settings.toBlockingSettings()

/** Initialize the settings store */
fun setupAppSettings() {
	if (!blockingSettings.getBoolean("setup", false)) {
		blockingSettings.putBoolean("logged_in", false)
		blockingSettings.putBoolean("setup", true)
	}

	if (getCurrentAccountId() != "" && getCurrentAccountHost() == "")
		logoutAccount(getCurrentAccountId())
}

//<editor-fold name="Account State">
/** Get a list of account IDs that are currently logged in */
fun getAccounts() = blockingSettings.getString("accounts", "").split(" ").filter { !it.isBlank() }
/** Get ID of current user */
fun getCurrentAccountId() = blockingSettings.getString("current_account", "")
/** Get host/instance of the current user */
fun getCurrentAccountHost() = getAccountHost(getCurrentAccountId())
/** Get host/instance of any logged in user */
fun getAccountHost(id: String) = blockingSettings.getString("account_${id}_host", "")

/**
 * Sets up settings state to log out the specified account ID.
 * After running, navigate to [site.remlit.snowdrop.StartRoute] to properly route the user.
 *
 * @param accountId Account ID of user to log out.
 * @since 0.0.1-alpha
 * */
fun logoutAccount(accountId: String) {
	blockingSettings.putBoolean("logged_in", false)
	blockingSettings.remove("current_account")
	blockingSettings.remove("account_${accountId}_host")
	blockingSettings.remove("account_${accountId}_token")

	blockingSettings.putString(
		"accounts",
		getAccounts().filter { it != accountId }
			.joinToString(" ")
	)
}

/** Debug only function to toggle logged in state. */
fun toggleLoggedInState() {
	val current = blockingSettings.getBoolean("logged_in", false)
	blockingSettings.putBoolean("logged_in", !current)
}

fun addNewAccount(navController: NavController) {
	blockingSettings.putBoolean("logged_in", false)
	blockingSettings.remove("current_account")
	navController.navigate(StartRoute)
}

fun switchAccount(accountId: String, navController: NavController) {
	blockingSettings.putString("current_account", accountId)
	navController.navigate(StartRoute)
}

/**
 * Gets an account's user object from the verify credentials endpoint.
 *
 * @param id ID of account
 *
 * @return User flow
 * @since 0.0.2-alpha
 * */
@OptIn(ExperimentalSettingsApi::class)
fun getAccountObjectFlow(id: String): Flow<Account?> = flow {
	if (!getAccounts().contains(id))
		emit(null)

	if (getCacheEntry(id, "account_$id") == null)
		emit(null)

	val account = getCacheEntry(id, "account_$id")?.getContent<Account>()
	if (account != null) emit(account)
}


/**
 * Gets the current account's user object from the verify credentials endpoint.
 *
 * @return User
 * @since 0.0.1-alpha
 * */
@OptIn(ExperimentalSettingsApi::class)
fun getCurrentAccountObjectFlow(): Flow<Account> = flow {
	if (!settings.getBoolean("logged_in", false))
		return@flow

	if (getCacheEntry("account_${getCurrentAccountId()}") == null)
		updateCurrentAccountObject()

	val account = getCacheEntry("account_${getCurrentAccountId()}")?.getContent<Account>()
	if (account != null) emit(account)
}

suspend fun updateCurrentAccountObject() {
	val res = verifyCredentials()
	if (res.error || res.response == null) return

	putCacheEntry(
		"account_${getCurrentAccountId()}",
		res.response
	)
}
//</editor-fold>

//<editor-fold name="Specific Settings">
// default visibility
/**
 * Get default visibility setting for current user.
 * @since 0.0.2-alpha
 * */
@OptIn(ExperimentalSettingsApi::class)
fun getDefaultVisibility() = settings.getStringFlow("default_visibility_${getCurrentAccountId()}", "public")

/**
 * Put default visibility setting for current user.
 * @param value Visibility
 * @since 0.0.2-alpha
 * */
fun putDefaultVisibility(value: String) = blockingSettings.putString("default_visibility_${getCurrentAccountId()}", value)

// bottom bar order
val defaultNavigationBarOrder = listOf(NavigationBarOption.Timeline, NavigationBarOption.Notifications, NavigationBarOption.Explore,
	NavigationBarOption.MyProfile).joinToString(separator = " ")

@OptIn(ExperimentalSettingsApi::class)
fun getNavigationBarOrder() = settings.getStringFlow("bottom_bar_order", defaultNavigationBarOrder)

fun getNavigationBarOrderBlocking() = blockingSettings.getString("bottom_bar_order", defaultNavigationBarOrder)

@OptIn(ExperimentalSettingsApi::class)
fun putNavigationBarOrder(value: String) = blockingSettings.putString("bottom_bar_order", value)
//</editor-fold>


/*
* Global mutable states
* */

/** Used for compose post FAB */
var scrollingUpward by mutableStateOf(true)

var showAccountSwitcher by mutableStateOf(false)
