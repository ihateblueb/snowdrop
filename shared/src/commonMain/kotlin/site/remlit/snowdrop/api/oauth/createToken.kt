package site.remlit.snowdrop.api.oauth

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.forms.submitForm
import io.ktor.http.parameters
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.response.OauthToken
import site.remlit.snowdrop.util.endOfRequest
import site.remlit.snowdrop.util.getCurrentAccountHost
import site.remlit.snowdrop.util.getCurrentAccountId
import site.remlit.snowdrop.util.httpClient
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun createToken(code: String): ApiResponse<OauthToken> = safeApiRequest {
	val accountId = getCurrentAccountId()
	val host = getCurrentAccountHost()
	val clientId = settings.getString("account_${accountId}_client_id", "")
	val clientSecret = settings.getString("account_${accountId}_client_secret", "")

	val req = httpClient.submitForm("https://$host/oauth/token", parameters {
		append("client_id", clientId)
		append("client_secret", clientSecret)
		append("redirect_uri", redirectUri)
		append("grant_type", "authorization_code")
		append("scope", authScopes)
		append("code", code)

	})

	endOfRequest(req)
}