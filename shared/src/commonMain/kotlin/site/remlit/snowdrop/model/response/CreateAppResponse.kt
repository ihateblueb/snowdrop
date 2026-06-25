package site.remlit.snowdrop.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateAppResponse(
	val id: String,
	val name: String,
	val website: String,
	val scopes: List<String> = listOf(),

	@SerialName("redirect_uri")
	val redirectUri: String,
	@SerialName("redirect_uris")
	val redirectUris: List<String>? = null,

	@SerialName("client_id")
	val clientId: String,
	@SerialName("client_secret")
	val clientSecret: String,
	@SerialName("client_secret_expires_at")
	val clientSecretExpiresAt: String? = null,
)
