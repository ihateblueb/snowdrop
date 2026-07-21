package site.remlit.snowdrop.model

import kotlinx.serialization.Serializable

@Serializable
data class StatusContext(
	val ancestors: List<Status>,
	val descendants: List<Status>
)
