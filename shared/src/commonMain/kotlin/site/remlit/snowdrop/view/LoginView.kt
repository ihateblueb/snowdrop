package site.remlit.snowdrop.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.DebugRoute
import site.remlit.snowdrop.TimelineRoute
import site.remlit.snowdrop.api.oauth.authScopes
import site.remlit.snowdrop.api.oauth.createApp
import site.remlit.snowdrop.api.oauth.createToken
import site.remlit.snowdrop.api.oauth.redirectUri
import site.remlit.snowdrop.api.verifyCredentials
import site.remlit.snowdrop.component.AccountPickerList
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.LocalSnackbarController
import site.remlit.snowdrop.util.bg
import site.remlit.snowdrop.util.bgIO
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.determineFeatures
import site.remlit.snowdrop.util.getAccountObject
import site.remlit.snowdrop.util.getAccounts
import site.remlit.snowdrop.util.listItemClip
import site.remlit.snowdrop.util.logoutAccount
import site.remlit.snowdrop.util.settings
import site.remlit.snowdrop.util.switchAccount
import site.remlit.snowdrop.util.updateCurrentAccountObject
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources._continue
import snowdrop.shared.generated.resources.debug
import snowdrop.shared.generated.resources.icon_snowdrop_24
import snowdrop.shared.generated.resources.icon_snowdrop_36
import snowdrop.shared.generated.resources.instance_host
import snowdrop.shared.generated.resources.ok
import snowdrop.shared.generated.resources.reset
import snowdrop.shared.generated.resources.you_must_provide_a_valid_host
import kotlin.uuid.Uuid

@Composable
@OptIn(ExperimentalSettingsApi::class)
fun LoginView() = ViewSurface {
	val navController = LocalNavController.current
	val uriHandler = LocalUriHandler.current
	val snackbarHandler = LocalSnackbarController.current


	// Text field states
	var host by remember { mutableStateOf("") }

	// Error states
	var showHostError by remember { mutableStateOf(false) }

	// Auth flow states
	var waitingForNext by remember { mutableStateOf(false) }
	var continued by remember { mutableStateOf(false) }

	// Account states
	val currentAccountId by settings.getStringOrNullFlow("current_account")
		.collectAsStateWithLifecycle(null)

	val oauthCallbackCode by settings.getStringOrNullFlow("oauth_callback")
		.collectAsStateWithLifecycle(null)

	fun continueButtonPressed() {
		if (host.isBlank()) {
			showHostError = true
			return
		}
		// todo: validation

		waitingForNext = true

		bg {
			val existingAccounts = settings.getString("accounts", "")
			val accountId = "_S-${Uuid.random()}"
			settings.putString("accounts", "$existingAccounts $accountId")
			settings.putString("current_account", accountId)
			settings.putString("account_${accountId}_host", host)

			// get link you must visit to get token
			val res = createApp()
			if (res.error || res.response == null) {
				res.handleError(snackbarHandler)
				return@bg
			}

			settings.putString("account_${accountId}_token", "")
			settings.putString("account_${accountId}_client_id", res.response.clientId)
			settings.putString("account_${accountId}_client_secret", res.response.clientSecret)

			continued = true

			val authLink = "https://${host}/oauth/authorize"+
				"?response_type=code"+
				"&redirect_uri=$redirectUri"+
				"&scope=$authScopes"+
				"&client_id=${res.response.clientId}"

			uriHandler.openUri(authLink)
		}
	}

	fun finishButtonPressed() = runBlocking {
		val res = createToken(oauthCallbackCode!!)
		Logger.d { res.toString() }
		blockingSettings.remove("oauth_callback")

		if (res.error || res.response == null) {
			res.handleError(snackbarHandler)
			return@runBlocking
		}

		//<editor-fold name="existing account check">
		val existingAccounts = getAccounts().map { Pair(it, getAccountObject(it)) }
			.filter { it.second != null }

		val verifyCredentialsRes = verifyCredentials(
			host = settings.getString("account_${currentAccountId}_host", host),
			token = res.response.accessToken
		)
		if (verifyCredentialsRes.error || verifyCredentialsRes.response == null) {
			res.handleError(snackbarHandler)
			return@runBlocking
		}

		// if the account exists, update to a new token and use the existing entry
		val existingAccount = existingAccounts.find {
			it.second?.id == verifyCredentialsRes.response.id
		}
		if (existingAccount != null) {
			logoutAccount(currentAccountId!!)
			blockingSettings.putString("account_${existingAccount.first}_token", res.response.accessToken)
			switchAccount(existingAccount.first, navController)
			blockingSettings.putBoolean("logged_in", true)
			return@runBlocking
		}
		//</editor-fold>

		blockingSettings.putString("account_${currentAccountId}_token", res.response.accessToken)
		blockingSettings.putBoolean("logged_in", true)

		bgIO {
			updateCurrentAccountObject()
			determineFeatures()
		}

		navController.navigate(TimelineRoute)
	}

	if (!oauthCallbackCode.isNullOrBlank())
		finishButtonPressed()

	if (!continued) {
		Column(
			modifier = Modifier
				.background(MaterialTheme.colorScheme.background)
				.safeContentPadding()
				.fillMaxSize(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
		) {
			Column(
				verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
				horizontalAlignment = Alignment.CenterHorizontally,
				modifier = Modifier.padding(bottom = 15.dp)
			) {
				Icon(painterResource(Res.drawable.icon_snowdrop_36), null)

				Text(
					"Snowdrop",
					fontSize = 32.sp,
					fontWeight = FontWeight.Bold
				)
			}

			Column(
				verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				TextField(
					host,
					singleLine = true,
					onValueChange = { host = it },
					keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
					keyboardActions = KeyboardActions(onGo = { continueButtonPressed() }),
					label = { Text(stringResource(Res.string.instance_host)) },
					placeholder = { Text("mastodon.social") }
				)

				if (showHostError) {
					AlertDialog(
						text = { Text(stringResource(Res.string.you_must_provide_a_valid_host)) },
						onDismissRequest = { showHostError = false },
						confirmButton = {
							TextButton(
								onClick = { showHostError = false }
							) {
								Text(stringResource(Res.string.ok))
							}
						},
						properties = DialogProperties(
							dismissOnBackPress = true,
							dismissOnClickOutside = true
						)
					)
				}

				Button(
					onClick = { continueButtonPressed() }
				) {
					if (waitingForNext) Text("...")
					else Text(stringResource(Res.string._continue))
				}

				if (getAccounts().isNotEmpty()) {
					Column(
						verticalArrangement = Arrangement.spacedBy(5.dp),
						modifier = Modifier.padding(top = 15.dp)
					) {
						AccountPickerList(
							onSelect = { blockingSettings.putBoolean("logged_in", true) }
						)
					}
				}
			}

			TextButton(
				onClick = { navController.navigate(DebugRoute) },
			) {
				Text(stringResource(Res.string.debug))
			}
		}
	} else {
		Column(
			modifier = Modifier
				.background(MaterialTheme.colorScheme.background)
				.safeContentPadding()
				.fillMaxSize(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(10.dp)
			) {
				CircularProgressIndicator()

				TextButton(
					modifier = Modifier
						.padding(top = 20.dp),
					onClick = {
						continued = false
						waitingForNext = false
						host = ""
					},
				) {
					Text(stringResource(Res.string.reset))
				}
			}
		}
	}
}
