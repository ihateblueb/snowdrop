package site.remlit.snowdrop.api

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.get
import io.ktor.client.request.header
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Emoji
import site.remlit.snowdrop.util.config.endOfRequest
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun getEmojis(): ApiResponse<List<Emoji>> = safeApiRequest { accountId, host ->
	val token = settings.getString("account_${accountId}_token", "")
	val req = httpClient.get("https://$host/api/v1/custom_emojis") {
		header("Authorization", "Bearer $token")
	}

	endOfRequest(req)
}
