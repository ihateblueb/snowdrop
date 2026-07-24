package site.remlit.snowdrop.api

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.util.StringValues
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Account
import site.remlit.snowdrop.util.config.endOfRequest
import site.remlit.snowdrop.util.getCurrentAccountHost
import site.remlit.snowdrop.util.getCurrentAccountId
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun verifyCredentials(
	accountId: String = getCurrentAccountId(),
	host: String = getCurrentAccountHost(),
	token: String? = null
): ApiResponse<Account> = safeApiRequest {
	val token = token ?: settings.getString("account_${accountId}_token", "")

	val req = httpClient.get("https://$host/api/v1/accounts/verify_credentials") {
		header("Authorization", "Bearer $token")
	}

	endOfRequest(req)
}
