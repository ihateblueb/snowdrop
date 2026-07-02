package site.remlit.snowdrop.api.accounts

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
suspend fun getStatuses(
	userId: String,
	limit: Int = 30,

	maxId: String? = null,
	sinceId: String? = null,
	offset: Int? = null,
	minId: String? = null,
	tagged: String? = null,

	pinned: Boolean? = null,
	onlyMedia: Boolean? = null,
	excludeReplies: Boolean? = null,
	excludeReblogs: Boolean? = null,
	excludeDirect: Boolean? = null,
): ApiResponse<List<Status>> = safeApiRequest {
	val accountId = getCurrentAccountId()
	val host = getCurrentAccountHost()
	val token = settings.getString("account_${accountId}_token", "")

	val req = httpClient.get("https://$host/api/v1/accounts/$userId/statuses") {
		header("Authorization", "Bearer $token")

		parameter("limit", limit)
		if (maxId != null) parameter("max_id", maxId)
		if (sinceId != null) parameter("since_id", sinceId)
		if (offset != null) parameter("offset", offset)
		if (minId != null) parameter("min_id", minId)
		if (tagged != null) parameter("tagged", tagged)

		if (pinned != null) parameter("pinned", pinned)
		if (onlyMedia != null) parameter("only_media", onlyMedia)
		if (excludeReplies != null) parameter("exclude_replies", excludeReplies)
		if (excludeReblogs != null) parameter("exclude_reblogs", excludeReblogs)
		if (excludeDirect != null) parameter("exclude_direct", excludeDirect)
	}

	endOfRequest(req)
}
