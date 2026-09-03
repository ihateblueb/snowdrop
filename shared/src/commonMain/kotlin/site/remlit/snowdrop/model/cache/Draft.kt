package site.remlit.snowdrop.model.cache

import kotlinx.serialization.Serializable

@Serializable
data class Draft(
	val id: String,
	val timestamp: String,
	val contentWarning: String? = null,
	val content: String? = null,
)
