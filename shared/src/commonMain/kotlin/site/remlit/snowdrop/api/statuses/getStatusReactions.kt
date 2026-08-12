package site.remlit.snowdrop.api.statuses

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Reaction
import site.remlit.snowdrop.util.config.endOfRequest
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun getStatusReactions(
	id: String,

	limit: Int = 45,
	emoji: String? = null,
	maxId: String? = null,
	minId: String? = null,
	sinceId: String? = null,
	offset: Int? = null,
): ApiResponse<List<Reaction>> = safeApiRequest { accountId, host ->
	val token = settings.getString("account_${accountId}_token", "")

	val req = httpClient.get("https://$host/api/v1/statuses/$id/reactions") {
		header("Authorization", "Bearer $token")

		parameter("limit", limit)

		if (emoji != null) parameter("emoji", emoji)
		if (maxId != null) parameter("max_id", maxId)
		if (sinceId != null) parameter("since_id", sinceId)
		if (offset != null) parameter("offset", offset)
		if (minId != null) parameter("min_id", minId)
	}

	endOfRequest(req)
}
