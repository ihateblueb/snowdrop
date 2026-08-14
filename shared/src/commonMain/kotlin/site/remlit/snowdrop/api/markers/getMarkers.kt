package site.remlit.snowdrop.api.markers

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Markers
import site.remlit.snowdrop.util.config.endOfRequest
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun getMarkers(
	timelines: List<String>,
): ApiResponse<Markers> = safeApiRequest { accountId, host ->
	val token = settings.getString("account_${accountId}_token", "")

	val req = httpClient.get("https://$host/api/v1/markers") {
		header("Authorization", "Bearer $token")

		timelines.forEach { parameter("timeline[]", it) }
	}

	endOfRequest(req)
}
