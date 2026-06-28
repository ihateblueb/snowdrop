package site.remlit.snowdrop.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toBlockingSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import site.remlit.snowdrop.StartRoute
import site.remlit.snowdrop.api.verifyCredentials
import site.remlit.snowdrop.model.Account
import site.remlit.snowdrop.util.cache.getCacheEntry
import site.remlit.snowdrop.util.cache.putCacheEntry

@OptIn(ExperimentalSettingsApi::class)
expect val settings: FlowSettings

@OptIn(ExperimentalSettingsApi::class)
val blockingSettings = settings.toBlockingSettings()

fun getAccounts() = blockingSettings.getString("accounts", "").split(" ").filter { !it.isBlank() }
fun getCurrentAccountId() = blockingSettings.getString("current_account", "")
fun getCurrentAccountHost() = blockingSettings.getString("account_${getCurrentAccountId()}_host", "")

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

fun setupAppSettings() {
	if (!blockingSettings.getBoolean("setup", false)) {
		blockingSettings.putBoolean("logged_in", false)
		blockingSettings.putBoolean("setup", true)
	}

	if (getCurrentAccountId() != "" && getCurrentAccountHost() == "")
		logoutAccount(getCurrentAccountId())
}

/**
 * Gets the current account's user object from the verify credentials endpoint.
 * @return User
 * */
@OptIn(ExperimentalSettingsApi::class)
fun getAccountObjectFlow(id: String): Flow<Account?> = flow {
	if (!getAccounts().contains(id))
		emit(null)

	if (getCacheEntry(id, "account_$id") == null)
		emit(null)

	safe {
		emit(
			getCacheEntry(id, "account_$id")!!
				.getContent<Account>()
		)
	}
}


/**
 * Gets the current account's user object from the verify credentials endpoint.
 * @return User
 * */
@OptIn(ExperimentalSettingsApi::class)
fun getCurrentAccountObjectFlow(): Flow<Account> = flow {
	if (!settings.getBoolean("logged_in", false))
		return@flow

	if (getCacheEntry("account_${getCurrentAccountId()}") == null)
		updateCurrentAccountObject()

	safe {
		emit(
			getCacheEntry("account_${getCurrentAccountId()}")!!
				.getContent<Account>()
		)
	}
}

suspend fun updateCurrentAccountObject() {
	val verifyRes = verifyCredentials()
	if (verifyRes.error) return
	if (verifyRes.response !is Account) return

	putCacheEntry(
		"account_${getCurrentAccountId()}",
		verifyRes.response
	)
}

/** Used for compose post FAB */
var scrollingUpward by mutableStateOf(true)

var showAccountSwitcher by mutableStateOf(false)
