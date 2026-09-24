package site.remlit.snowdrop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusSource(
    val id: String,
	val text: String,
	@SerialName("spoiler_text")
	val spoilerText: String
)
