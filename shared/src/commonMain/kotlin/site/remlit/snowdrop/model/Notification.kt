package site.remlit.snowdrop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import site.remlit.snowdrop.util.safeReturnable
import kotlin.time.Instant

@Serializable
data class Notification(
	override val id: String,
	val type: String,

	val account: Account,
	val status: Status? = null,

	val emoji: String? = null,
	@SerialName("emoji_url")
	val emojiUrl: String? = null,

	val name: String? = null,

	val reaction: ChuckyaReaction? = null,

	val bite: Bite? = null,

	@SerialName("created_at")
	val createdAt: String
) : IdentifiableObject<String> {
	@Serializable
	data class ChuckyaReaction(
		val name: String? = null,
		val id: String? = null,
		val url: String? = null,
		@SerialName("static_url")
		val staticUrl: String? = null
	)
	@Serializable
	data class Bite(
		val id: String,
		@SerialName("bite_back")
		val biteBack: Boolean = false
	)

	// Instants
	fun getCreatedAtTimestamp(): Instant? = safeReturnable {
		Instant.parse(this.createdAt)
	}
}
