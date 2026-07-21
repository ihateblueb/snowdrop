package site.remlit.snowdrop.model.request

import kotlinx.serialization.Serializable

@Serializable
data class PollVoteRequest(
	val choices: List<Int>
)
