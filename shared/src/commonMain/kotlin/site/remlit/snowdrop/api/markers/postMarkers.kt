package site.remlit.snowdrop.api.markers

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Marker
import site.remlit.snowdrop.util.config.endOfRequest
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun postMarkers(
	markers: Map<String, Marker>,
): ApiResponse<Map<String, Marker>> = safeApiRequest { accountId, host ->
	val token = settings.getString("account_${accountId}_token", "")

	val req = httpClient.post("https://$host/api/v1/markers") {
		header("Authorization", "Bearer $token")
		header("Content-Type", "application/json")

		setBody(markers)
	}

	endOfRequest(req)
}
