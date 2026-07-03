package site.remlit.snowdrop.api.timeline

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.config.endOfRequest
import site.remlit.snowdrop.util.getCurrentAccountHost
import site.remlit.snowdrop.util.getCurrentAccountId
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.getFeature
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun getBubbleTimeline(
	limit: Int = 30,

	maxId: String? = null,
	sinceId: String? = null,
	offset: Int? = null,
	minId: String? = null,

	local: Boolean? = null,
	remote: Boolean? = null,
	onlyMedia: Boolean? = null
): ApiResponse<List<Status>> = safeApiRequest {
	if (getFeature("bubble_timeline_chuckya")) {
		return getPublicTimeline(maxId = maxId, minId = minId, sinceId = sinceId, bubble = true)
	}

	val accountId = getCurrentAccountId()
	val host = getCurrentAccountHost()
	val token = settings.getString("account_${accountId}_token", "")

	val req = httpClient.get("https://$host/api/v1/timelines/bubble") {
		header("Authorization", "Bearer $token")

		parameter("limit", limit)

		if (maxId != null) parameter("max_id", maxId)
		if (sinceId != null) parameter("since_id", sinceId)
		if (offset != null) parameter("offset", offset)
		if (minId != null) parameter("min_id", minId)

		if (local != null) parameter("local", local)
		if (remote != null) parameter("remote", remote)
		if (onlyMedia != null) parameter("only_media", onlyMedia)
	}

	endOfRequest(req)
}
