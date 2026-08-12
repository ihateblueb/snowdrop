package site.remlit.snowdrop.api.followRequest

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.header
import io.ktor.client.request.post
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Relationship
import site.remlit.snowdrop.util.config.endOfRequest
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun rejectFollowRequest(id: String): ApiResponse<Relationship> = safeApiRequest { accountId, host ->
	val token = settings.getString("account_${accountId}_token", "")

	val req = httpClient.post("https://$host/api/v1/follow_requests/$id/reject") {
		header("Authorization", "Bearer $token")
	}

	endOfRequest(req)
}
