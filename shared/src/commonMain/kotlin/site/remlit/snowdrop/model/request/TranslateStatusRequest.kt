package site.remlit.snowdrop.model.request

import kotlinx.serialization.Serializable

@Serializable
data class TranslateStatusRequest(
	val lang: String
)
