package site.remlit.snowdrop.api.compat.pleroma.notifications

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.util.config.endOfRequestNoBody
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings


@OptIn(ExperimentalSettingsApi::class)
suspend fun readNotifications(id: String): ApiResponse<Unit> = safeApiRequest { currentAccountId, host ->
	val token = settings.getString("account_${currentAccountId}_token", "")

	val req = httpClient.post("https://$host/api/v1/pleroma/notifications/read") {
		header("Authorization", "Bearer $token")
		header("Content-Type", "application/json")

		setBody(mapOf("id" to id))
	}

	endOfRequestNoBody(req)
}
