package site.remlit.snowdrop.model

import kotlinx.serialization.Serializable

@Serializable
data class Markers(
	val notifications: Marker? = null
)
