package site.remlit.snowdrop.api.notifications

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.*
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Notification
import site.remlit.snowdrop.util.config.endOfRequest
import site.remlit.snowdrop.util.getCurrentAccountHost
import site.remlit.snowdrop.util.getCurrentAccountId
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun getNotifications(): ApiResponse<List<Notification>> = safeApiRequest {
	val accountId = getCurrentAccountId()
	val host = getCurrentAccountHost()
	val token = settings.getString("account_${accountId}_token", "")

	val req = httpClient.get("https://$host/api/v1/notifications") {
		header("Authorization", "Bearer $token")
	}

	endOfRequest(req)
}
