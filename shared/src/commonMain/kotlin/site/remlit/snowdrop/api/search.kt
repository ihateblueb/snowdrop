package site.remlit.snowdrop.api

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.response.SearchResponse
import site.remlit.snowdrop.util.config.endOfRequest
import site.remlit.snowdrop.util.getCurrentAccountHost
import site.remlit.snowdrop.util.getCurrentAccountId
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun search(
	query: String,

	type: String? = null,
	resolve: Boolean? = null,
	following: Boolean? = null,
	accountId: String? = null,
	excludeUnreviewed: Boolean? = null,

	maxId: String? = null,
	sinceId: String? = null,
	minId: String? = null,
	offset: Int? = null,
	limit: Int? = null,
): ApiResponse<SearchResponse> = safeApiRequest {
	val currentAccountId = getCurrentAccountId()
	val host = getCurrentAccountHost()
	val token = settings.getString("account_${currentAccountId}_token", "")

	val req = httpClient.get("https://$host/api/v2/search") {
		header("Authorization", "Bearer $token")

		parameter("q", query)
		if (type != null) parameter("type", type)
		if (resolve != null) parameter("resolve", resolve)
		if (following != null) parameter("following", following)
		if (accountId != null) parameter("account_id", accountId)
		if (excludeUnreviewed != null) parameter("exclude_unreviewed", excludeUnreviewed)

		if (maxId != null) parameter("max_id", maxId)
		if (sinceId != null) parameter("since_id", sinceId)
		if (minId != null) parameter("min_id", minId)
		if (offset != null) parameter("offset", offset)
		if (limit != null) parameter("limit", limit)
	}

	endOfRequest(req)
}
