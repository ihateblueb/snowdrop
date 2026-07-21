package site.remlit.snowdrop.model.response

import kotlinx.serialization.Serializable
import site.remlit.snowdrop.model.Account
import site.remlit.snowdrop.model.IdentifiableObject
import site.remlit.snowdrop.model.Status
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
@OptIn(ExperimentalUuidApi::class)
data class SearchResponse(
	override val id: String = Uuid.generateV4().toString(), // necessary for RefreshableTimeline
	val accounts: List<Account> = emptyList(),
	val statuses: List<Status> = emptyList(),
	val hashtags: List<Hashtag> = emptyList()
) : IdentifiableObject<String> {
	@Serializable
	data class Hashtag(
		val name: String,
		val url: String,
		val following: Boolean = false,
		val history: List<String>? = emptyList()
	)
}
