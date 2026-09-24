package site.remlit.snowdrop.model.request

import kotlinx.serialization.Serializable

@Serializable
data class MuteAccountRequest(
	val notifications: Boolean = true,
	val duration: Int = 0
)
