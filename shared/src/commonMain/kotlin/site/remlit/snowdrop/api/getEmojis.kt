package site.remlit.snowdrop.api

import io.ktor.client.request.get
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Emoji
import site.remlit.snowdrop.util.config.endOfRequest
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.safeApiRequest

suspend fun getEmojis(): ApiResponse<List<Emoji>> = safeApiRequest { _, host ->
	val req = httpClient.get("https://$host/api/v1/custom_emojis")

	endOfRequest(req)
}
