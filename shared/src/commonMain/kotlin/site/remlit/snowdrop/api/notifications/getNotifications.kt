package site.remlit.snowdrop.api.notifications

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.*
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Notification
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.config.endOfRequest
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun getNotifications(
	limit: Int = blockingSettings.getInt("notifs_per_page", 100),

	maxId: String? = null,
	sinceId: String? = null,
	offset: Int? = null,
	minId: String? = null,
	types: List<String>? = null,
	excludeTypes: List<String>? = null,
	accountId: String? = null,
): ApiResponse<List<Notification>> = safeApiRequest { currentAccountId, host ->
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
