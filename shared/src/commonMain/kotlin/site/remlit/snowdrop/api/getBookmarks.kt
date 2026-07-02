package site.remlit.snowdrop.api

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.config.endOfRequest
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.getCurrentAccountHost
import site.remlit.snowdrop.util.getCurrentAccountId
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun getBookmarks(
	limit: Int = 30,

	maxId: String? = null,
	sinceId: String? = null,
	offset: Int? = null,
	minId: String? = null,
): ApiResponse<List<Status>> = safeApiRequest {
	val currentAccountId = getCurrentAccountId()
	val host = getCurrentAccountHost()
	val token = settings.getString("account_${currentAccountId}_token", "")

	val req = httpClient.get("https://$host/api/v1/bookmarks") {
		header("Authorization", "Bearer $token")

		parameter("limit", limit)

		if (maxId != null) parameter("max_id", maxId)
		if (sinceId != null) parameter("since_id", sinceId)
		if (offset != null) parameter("offset", offset)
		if (minId != null) parameter("min_id", minId)
	}

	endOfRequest(req)
}
