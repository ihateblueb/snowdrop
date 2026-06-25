package site.remlit.snowdrop.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toBlockingSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import site.remlit.snowdrop.api.verifyCredentials
import site.remlit.snowdrop.model.User

@OptIn(ExperimentalSettingsApi::class)
expect val settings: FlowSettings

@OptIn(ExperimentalSettingsApi::class)
val blockingSettings = settings.toBlockingSettings()

fun getAccounts() = blockingSettings.getString("accounts", "")
fun getCurrentAccountId() = blockingSettings.getString("current_account", "")
fun getCurrentAccountHost() = blockingSettings.getString("account_${getCurrentAccountId()}_host", "")

fun logoutAccount(accountId: String) {
	blockingSettings.remove("account_${accountId}_host")
	blockingSettings.remove("account_${accountId}_token")

	blockingSettings.putString(
		"accounts",
		getAccounts().split(" ").filter { it != accountId }
			.joinToString(" ")
	)
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
fun getCurrentAccountObjectFlow(): Flow<User> = object : Flow<User> {
	override suspend fun collect(collector: FlowCollector<User>) = safe {
		if (getCurrentAccountId() == "")
			return@safe

		if (settings.getStringOrNull("account_${getCurrentAccountId()}_user") == null)
			updateCurrentAccountObject()

		val user = json.decodeFromString<User>(
			settings.getString("account_${getCurrentAccountId()}_user", "")
		)

		collector.emit(user)
	}
}

suspend fun updateCurrentAccountObject(token: String? = null) {
	val verifyRes = verifyCredentials(token)
	if (verifyRes.error) return
	if (verifyRes.response !is User) return

	blockingSettings.putString(
		"account_${getCurrentAccountId()}_user",
		json.encodeToString(verifyRes.response)
	)
	blockingSettings.putString("account_${getCurrentAccountId()}_token", token!!)
}

/** Used for compose post FAB,  */
var scrollingUpward by mutableStateOf(true)
