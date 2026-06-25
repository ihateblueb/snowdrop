package site.remlit.snowdrop.model.response

import kotlinx.serialization.Serializable
import site.remlit.snowdrop.model.User

@Serializable
data class SearchResponse(
	val accounts: List<User> = emptyList(),
	val statuses: List<String> = emptyList(),
	val hashtags: List<Hashtag> = emptyList()
) {
	@Serializable
	data class Hashtag(
		val name: String,
		val url: String,
		val following: Boolean,
		val history: List<String> = emptyList()
	)
}
