package site.remlit.snowdrop.api.notifications

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.*
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.GroupedNotificationsResults
import site.remlit.snowdrop.model.Notification
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.config.endOfRequest
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun getGroupedNotifications(
	limit: Int = 80,

	maxId: String? = null,
	sinceId: String? = null,
	minId: String? = null,
	types: List<String>? = null,
	excludeTypes: List<String>? = null,
	accountId: String? = null,
	expandAccount: String? = null,
	groupedTypes: List<String>? = null,
	includeFiltered: Boolean? = null,
	supportedTypes: List<String>? = null
): ApiResponse<GroupedNotificationsResults> = safeApiRequest { currentAccountId, host ->
	val token = settings.getString("account_${currentAccountId}_token", "")

	val req = httpClient.get("https://$host/api/v2/notifications") {
		header("Authorization", "Bearer $token")

		// holy parameter
		parameter("limit", limit)
		if (maxId != null) parameter("max_id", maxId)
		if (sinceId != null) parameter("since_id", sinceId)
		if (minId != null) parameter("min_id", minId)
		if (!types.isNullOrEmpty()) types.forEach { parameter("types[]", it) }
		if (!excludeTypes.isNullOrEmpty()) excludeTypes.forEach { parameter("exclude_types[]", it) }
		if (accountId != null) parameter("account_id", accountId)
		if (expandAccount != null) parameter("expand_account", expandAccount)
		if (!groupedTypes.isNullOrEmpty()) groupedTypes.forEach { parameter("grouped_types[]", it) }
		if (includeFiltered != null) parameter("include_filtered", includeFiltered)
		if (!supportedTypes.isNullOrEmpty()) supportedTypes.forEach { parameter("supported_types[]", it) }
	}

	endOfRequest(req)
}
