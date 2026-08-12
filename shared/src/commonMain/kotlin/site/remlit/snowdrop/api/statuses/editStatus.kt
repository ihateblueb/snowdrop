package site.remlit.snowdrop.api.statuses

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.model.request.CreateStatusRequest
import site.remlit.snowdrop.util.config.endOfRequest
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun editStatus(
	id: String,
	req: CreateStatusRequest
): ApiResponse<Status> = safeApiRequest {  accountId, host ->
	req.validate()

	val token = settings.getString("account_${accountId}_token", "")

	val req = httpClient.put("https://$host/api/v1/statuses/$id") {
		header("Authorization", "Bearer $token")
		header("Content-Type", "application/json")
		setBody(req)
	}

	endOfRequest(req)
}
