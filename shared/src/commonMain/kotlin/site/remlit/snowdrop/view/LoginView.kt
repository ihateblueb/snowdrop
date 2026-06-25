package site.remlit.snowdrop.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import site.remlit.snowdrop.api.oauth.authScopes
import site.remlit.snowdrop.api.oauth.createApp
import site.remlit.snowdrop.api.oauth.createToken
import site.remlit.snowdrop.api.oauth.redirectUri
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.model.response.CreateAppResponse
import site.remlit.snowdrop.model.response.OauthToken
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.settings
import site.remlit.snowdrop.util.setupAppSettings
import site.remlit.snowdrop.util.updateCurrentAccountObject
import kotlin.uuid.Uuid

@Composable
@OptIn(ExperimentalSettingsApi::class)
fun LoginView(
	oauthCallbackCode: String? = null,
	navigateToTimeline: () -> Unit
) = ViewSurface {
	val uriHandler = LocalUriHandler.current


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

	val clientId by settings.getStringOrNullFlow("account_${currentAccountId}_client_id")
		.collectAsStateWithLifecycle(null)

	fun continueButtonPressed() {
		if (host.isBlank()) {
			showHostError = true
			return
		}
		// todo: validation

		waitingForNext = true

		// todo: this blocking is annoying
		runBlocking {
			val existingAccounts = blockingSettings.getString("accounts", "")
			val accountId = "_S-${Uuid.random()}"
			blockingSettings.putString("accounts", "$existingAccounts $accountId")
			blockingSettings.putString("current_account", accountId)
			blockingSettings.putString("account_${accountId}_host", host)

			// get link you must visit to get token
			val res = createApp()
			if (res.error) return@runBlocking
			if (res.response !is CreateAppResponse) return@runBlocking

			blockingSettings.putString("account_${accountId}_token", "")
			blockingSettings.putString("account_${accountId}_client_id", res.response.clientId)
			blockingSettings.putString("account_${accountId}_client_secret", res.response.clientSecret)

			continued = true

			val authLink = "https://${host}/oauth/authorize"+
				"?response_type=code"+
				"&redirect_uri=$redirectUri"+
				"&scope=$authScopes"+
				"&client_id=${res.response.clientId}"

			uriHandler.openUri(authLink)
		}
	}

	fun finishButtonPressed() {
		runBlocking {
			val res = createToken(oauthCallbackCode!!)
			if (res.error) return@runBlocking
			if (res.response !is OauthToken) return@runBlocking

			blockingSettings.putString("account_${currentAccountId}_token", res.response.accessToken)
			blockingSettings.putBoolean("logged_in", true)

			updateCurrentAccountObject(res.response.accessToken)
		}

		navigateToTimeline()
	}

	if (oauthCallbackCode != "null" && oauthCallbackCode != null) {
		Logger.e("MEOW! CONTENT IS $oauthCallbackCode")
		finishButtonPressed()
	}

	if (!continued) {
		Column(
			modifier = Modifier
				.background(MaterialTheme.colorScheme.background)
				.safeContentPadding()
				.fillMaxSize(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			Text(
				"Snowdrop",
				fontSize = 25.sp,
				fontWeight = FontWeight.Bold,
				modifier = Modifier
					.padding(bottom = 25.dp)
			)

			TextField(
				host,
				singleLine = true,
				onValueChange = { host = it },
				keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
				keyboardActions = KeyboardActions(onGo = { continueButtonPressed() }),
				label = { Text("Instance Host") },
				placeholder = { Text("mastodon.social") }
			)

			if (showHostError) {
				AlertDialog(
					text = { Text("You must provide a valid host!") },
					onDismissRequest = { showHostError = false },
					confirmButton = {
						TextButton(
							onClick = { showHostError = false }
						) {
							Text("Ok")
						}
					},
					properties = DialogProperties(
						dismissOnBackPress = true,
						dismissOnClickOutside = true
					)
				)
			}

			Button(
				modifier = Modifier
					.padding(top = 10.dp),
				onClick = { continueButtonPressed() }
			) {
				if (waitingForNext) Text("...")
				else Text("Continue")
			}

			OutlinedButton(
				modifier = Modifier
					.padding(top = 20.dp),
				onClick = { blockingSettings.clear(); setupAppSettings() },
			) {
				Text("Clear all data")
			}
		}
	} else {
		Text("Finishing auth")
	}
}
