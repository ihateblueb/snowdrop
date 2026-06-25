package site.remlit.snowdrop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import site.remlit.snowdrop.util.safeReturnable
import kotlin.time.Instant

@Serializable
data class Notification(
	val id: String,
	val type: String,

	val account: User,
	val status: Status? = null,

	val emoji: String? = null,
	val reaction: ChuckyaReaction? = null,
	@SerialName("emoji_url")
	val emojiUrl: String? = null,
	val bite: Bite? = null,

	@SerialName("created_at")
	val createdAt: String
) {
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
