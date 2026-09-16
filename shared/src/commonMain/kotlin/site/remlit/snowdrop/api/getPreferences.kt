package site.remlit.snowdrop.api

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.get
import io.ktor.client.request.header
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Account
import site.remlit.snowdrop.model.Preferences
import site.remlit.snowdrop.util.config.endOfRequest
import site.remlit.snowdrop.util.getCurrentAccountHost
import site.remlit.snowdrop.util.getCurrentAccountId
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun getPreferences(): ApiResponse<Preferences> = safeApiRequest { accountId, host ->
	val token = settings.getString("account_${accountId}_token", "")
	val req = httpClient.get("https://$host/api/v1/preferences") {
		header("Authorization", "Bearer $token")
	}

	endOfRequest(req)
}
