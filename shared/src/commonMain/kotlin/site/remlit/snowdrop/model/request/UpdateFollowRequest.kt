package site.remlit.snowdrop.model.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateFollowRequest(
	val reblogs: Boolean? = null
)
