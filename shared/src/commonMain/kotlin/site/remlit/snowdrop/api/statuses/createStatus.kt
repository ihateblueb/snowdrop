package site.remlit.snowdrop.api.statuses

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.*
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.model.request.CreateStatusRequest
import site.remlit.snowdrop.util.config.endOfRequest
import site.remlit.snowdrop.util.getCurrentAccountHost
import site.remlit.snowdrop.util.getCurrentAccountId
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun createStatus(req: CreateStatusRequest): ApiResponse<Status> = safeApiRequest {
	req.validate()

	val accountId = getCurrentAccountId()
	val host = getCurrentAccountHost()
	val token = settings.getString("account_${accountId}_token", "")

	val req = httpClient.post("https://$host/api/v1/statuses") {
		header("Authorization", "Bearer $token")
		header("Content-Type", "application/json")
		setBody(req)
	}

	endOfRequest(req)
}
