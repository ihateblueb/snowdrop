package site.remlit.snowdrop.api.accounts

import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Relationship
import site.remlit.snowdrop.util.config.endOfRequest
import site.remlit.snowdrop.util.getCurrentAccountHost
import site.remlit.snowdrop.util.getCurrentAccountId
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings


@OptIn(ExperimentalSettingsApi::class)
suspend fun getRelationships(ids: List<String>): ApiResponse<List<Relationship>> = safeApiRequest {
	val accountId = getCurrentAccountId()
	val host = getCurrentAccountHost()
	val token = settings.getString("account_${accountId}_token", "")

	val req = httpClient.get("https://$host/api/v1/accounts/relationships") {
		header("Authorization", "Bearer $token")
		ids.forEach { parameter("id[]", it) }
	}

	endOfRequest(req)
}
