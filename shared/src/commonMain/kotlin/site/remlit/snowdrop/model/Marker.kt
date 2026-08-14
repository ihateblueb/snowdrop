package site.remlit.snowdrop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Marker(
	@SerialName("last_read_id")
	val lastReadId: String? = null,
	val version: Int? = null,
	@SerialName("updated_at")
	val updatedAt: String? = null
)
