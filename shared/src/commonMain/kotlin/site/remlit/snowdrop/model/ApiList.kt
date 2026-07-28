package site.remlit.snowdrop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiList(
	val id: String,
	val title: String,
	val exclusive: Boolean = false,
	@SerialName("replies_policy")
	val repliesPolicy: String? = null
)
