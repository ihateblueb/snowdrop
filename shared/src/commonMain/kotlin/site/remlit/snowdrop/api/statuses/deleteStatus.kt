package site.remlit.snowdrop.api.statuses

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.delete
import io.ktor.client.request.header
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.util.config.endOfRequestNoBody
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.getCurrentAccountHost
import site.remlit.snowdrop.util.getCurrentAccountId
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun deleteStatus(id: String): ApiResponse<Unit> = safeApiRequest {
	val accountId = getCurrentAccountId()
	val host = getCurrentAccountHost()
	val token = settings.getString("account_${accountId}_token", "")

	val req = httpClient.delete("https://$host/api/v1/statuses/$id") {
		header("Authorization", "Bearer $token")
	}

	endOfRequestNoBody(req)
}
