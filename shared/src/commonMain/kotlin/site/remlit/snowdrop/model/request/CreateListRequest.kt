package site.remlit.snowdrop.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateListRequest(
	val title: String,
	val exclusive: Boolean = false,
	@SerialName("replies_policy")
	val repliesPolicy: String? = null
)
