package site.remlit.snowdrop.api.media

import com.russhwolf.settings.ExperimentalSettingsApi
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absoluteFile
import io.github.vinceglb.filekit.mimeType
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.readString
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.headers
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.config.endOfRequest
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.getCurrentAccountHost
import site.remlit.snowdrop.util.getCurrentAccountId
import site.remlit.snowdrop.util.safeApiRequest
import site.remlit.snowdrop.util.settings

@OptIn(ExperimentalSettingsApi::class)
suspend fun uploadMedia(file: PlatformFile, alt: String?): ApiResponse<Status.MediaAttachment> = safeApiRequest {
	val accountId = getCurrentAccountId()
	val host = getCurrentAccountHost()
	val token = settings.getString("account_${accountId}_token", "")

	val req = httpClient.submitFormWithBinaryData(
		url = "https://$host/api/v1/media",
		formData = formData {
			append("file", file.readBytes(), headers {
				append("Content-Type", file.mimeType()?.toString() ?: "application/octet-stream")
			})
			if (alt != null) append("description", alt)
		}
	) {
		header("Authorization", "Bearer $token")
	}

	endOfRequest(req)
}
