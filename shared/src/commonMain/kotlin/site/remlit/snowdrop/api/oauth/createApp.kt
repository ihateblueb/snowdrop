package site.remlit.snowdrop.api.oauth

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.forms.submitForm
import io.ktor.http.parameters
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.response.CreateAppResponse
import site.remlit.snowdrop.util.endOfRequest
import site.remlit.snowdrop.util.getCurrentAccountHost
import site.remlit.snowdrop.util.httpClient
import site.remlit.snowdrop.util.safeApiRequest

const val authScopes = "read write push"
const val redirectUri = "snowdrop://oauth-callback"

@OptIn(ExperimentalSettingsApi::class)
suspend fun createApp(): ApiResponse<CreateAppResponse> = safeApiRequest {
	val host = getCurrentAccountHost()

	val req = httpClient.submitForm("https://$host/api/v1/apps", parameters {
		append("client_name", "Snowdrop")
		append("redirect_uris", redirectUri)
		append("scopes", authScopes)
		append("website", "https://github.com/ihateblueb/snowdrop")
	})

	endOfRequest(req)
}
