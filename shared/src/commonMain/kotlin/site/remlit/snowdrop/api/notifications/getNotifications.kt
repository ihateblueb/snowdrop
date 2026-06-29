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
suspend fun getNotifications(
	limit: Int = 100,

	maxId: String? = null,
	sinceId: String? = null,
	offset: Int? = null,
	minId: String? = null,
	types: List<String>? = null,
	excludeTypes: List<String>? = null,
	accountId: String? = null,
): ApiResponse<List<Notification>> = safeApiRequest {
	val currentAccountId = getCurrentAccountId()
	val host = getCurrentAccountHost()
	val token = settings.getString("account_${currentAccountId}_token", "")

	val req = httpClient.get("https://$host/api/v1/notifications") {
		header("Authorization", "Bearer $token")

		parameter("limit", limit)
		if (maxId != null) parameter("max_id", maxId)
		if (sinceId != null) parameter("since_id", sinceId)
		if (offset != null) parameter("offset", offset)
		if (minId != null) parameter("min_id", minId)
		if (!types.isNullOrEmpty()) types.forEach { parameter("types[]", it) }
		if (!excludeTypes.isNullOrEmpty()) excludeTypes.forEach { parameter("exclude_types[]", it) }
		if (accountId != null) parameter("account_id", accountId)
	}

	endOfRequest(req)
}
