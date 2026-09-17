package site.remlit.snowdrop.api.accounts

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Relationship
import site.remlit.snowdrop.model.request.UpdateFollowRequest
import site.remlit.snowdrop.util.config.endOfRequest
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun followAccount(
	id: String,
	req: UpdateFollowRequest? = null
): ApiResponse<Relationship> = safeApiRequest { accountId, host ->
	val token = settings.getString("account_${accountId}_token", "")

	val req = httpClient.post("https://$host/api/v1/accounts/$id/follow") {
		header("Authorization", "Bearer $token")

		if (req != null) {
			header("Content-Type", "application/json")
			setBody(req)
		}
	}

	endOfRequest(req)
}
