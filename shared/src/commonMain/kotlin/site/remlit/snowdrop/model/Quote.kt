package site.remlit.snowdrop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Quote(
	val state: String? = null,
	@SerialName("quoted_status")
	val quotedStatus: Status? = null,
)
