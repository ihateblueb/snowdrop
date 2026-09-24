package site.remlit.snowdrop.api

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.request.ReportRequest
import site.remlit.snowdrop.util.config.endOfRequestNoBody
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun report(req: ReportRequest): ApiResponse<Unit> = safeApiRequest { accountId, host ->
	val token = settings.getString("account_${accountId}_token", "")

	val req = httpClient.post("https://$host/api/v1/reports") {
		header("Authorization", "Bearer $token")
		header("Content-Type", "application/json")
		setBody(req)
	}

	endOfRequestNoBody(req)
}
