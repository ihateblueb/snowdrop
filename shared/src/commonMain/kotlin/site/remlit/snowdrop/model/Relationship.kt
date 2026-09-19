package site.remlit.snowdrop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Relationship(
	val id: String? = null,

	val following: Boolean = false,
	@SerialName("followed_by")
	val followedBy: Boolean = false,
	val requested: Boolean = false,

	val blocking: Boolean = false,
	@SerialName("blocked_by")
	val blockedBy: Boolean = false,

	val muting: Boolean = false,
	@SerialName("muting_notifications")
	val mutingNotifications: Boolean = false,
	@SerialName("muting_expires_at")
	val mutingExpiresAt: String? = null,

	@SerialName("showing_reblogs")
	val showingReblogs: Boolean = false,
	val notifying: Boolean = false,

	@SerialName("domain_blocking")
	val domainBlocking: Boolean = false,
	val endorsed: Boolean = false
)
