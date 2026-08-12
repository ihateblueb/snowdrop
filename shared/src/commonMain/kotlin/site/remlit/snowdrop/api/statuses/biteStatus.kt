package site.remlit.snowdrop.api.statuses

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.header
import io.ktor.client.request.post
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.util.config.endOfRequestNoBody
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun biteStatus(id: String): ApiResponse<Unit> = safeApiRequest { accountId, host ->
	val token = settings.getString("account_${accountId}_token", "")

	val req = httpClient.post("https://$host/api/v1/statuses/$id/bite") {
		header("Authorization", "Bearer $token")
	}

	endOfRequestNoBody(req)
}
