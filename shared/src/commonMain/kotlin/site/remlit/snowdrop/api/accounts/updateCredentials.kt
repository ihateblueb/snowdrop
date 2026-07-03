package site.remlit.snowdrop.api.accounts

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import site.remlit.snowdrop.model.Account
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.request.UpdateCredentialsRequest
import site.remlit.snowdrop.util.config.endOfRequest
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.getCurrentAccountHost
import site.remlit.snowdrop.util.getCurrentAccountId
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun updateCredentials(req: UpdateCredentialsRequest): ApiResponse<Account> = safeApiRequest {
	val accountId = getCurrentAccountId()
	val host = getCurrentAccountHost()
	val token = settings.getString("account_${accountId}_token", "")

	val req = httpClient.patch("https://$host/api/v1/accounts/update_credentials") {
		header("Authorization", "Bearer $token")

		header("Content-Type", "application/json")
		setBody(req)
	}

	endOfRequest(req)
}
