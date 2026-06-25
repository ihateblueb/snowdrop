package site.remlit.snowdrop.api

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.get
import io.ktor.client.request.header
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.User
import site.remlit.snowdrop.util.endOfRequest
import site.remlit.snowdrop.util.getCurrentAccountHost
import site.remlit.snowdrop.util.getCurrentAccountId
import site.remlit.snowdrop.util.httpClient
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun verifyCredentials(token: String? = null): ApiResponse<User> = safeApiRequest {
	val host = getCurrentAccountHost()
	if (token == null) {
		val accountId = getCurrentAccountId()
		val tkn = settings.getString("account_${accountId}_token", "")

		val req = httpClient.get("https://$host/api/v1/accounts/verify_credentials") {
			header("Authorization", "Bearer $tkn")
		}

		endOfRequest(req)
	} else {
		val req = httpClient.get("https://$host/api/v1/accounts/verify_credentials") {
			header("Authorization", "Bearer $token")
		}

		endOfRequest(req)
	}
}
