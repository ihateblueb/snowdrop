package site.remlit.snowdrop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
	val id: String,
	val username: String,
	val acct: String,
	val fqn: String,
	@SerialName("display_name")
	val displayName: String? = null,

	@SerialName("followers_count")
	val followersCount: Long = 0,
	@SerialName("following_count")
	val followingCount: Long = 0,
	@SerialName("statuses_count")
	val statusesCount: Long = 0,

	val note: String? = null,

	val url: String,
	val uri: String? = null,

	val avatar: String? = null,
	@SerialName("avatar_static")
	val avatarStatic: String? = null,
	@SerialName("avatar_description")
	val avatarDescription: String? = null,

	val header: String? = null,
	@SerialName("header_static")
	val headerStatic: String? = null,
	@SerialName("header_description")
	val headerDescription: String? = null,

	// todo: why is this a string? what is this?
	val moved: String? = null,

	val locked: Boolean,
	val bot: Boolean,
	@SerialName("is_cat")
	val isCat: Boolean = false,
	@SerialName("speak_as_cat")
	val speakAsCat: Boolean = false,
	val discoverable: Boolean,

	val fields: List<Field>,
	val source: Source? = null,
	val emojis: List<Emoji>,

	@SerialName("attribution_domains")
	val attributionDomains: List<String> = emptyList(),
	@SerialName("formatted_note")
	val formattedNote: String? = null,
	@SerialName("formatted_fields")
	val formattedFields: List<Field>? = emptyList(),

	@SerialName("created_at")
	val createdAt: String,
	@SerialName("last_status_at")
	val lastStatusAt: String? = null,
) {
	@Serializable
	data class Field(
		val name: String,
		val `value`: String,
		@SerialName("verified_at")
		val verifiedAt: String? = null
	)

	@Serializable
	data class Source(
		val lang: String? = null,
		val note: String? = null,
		val privacy: String? = null,
		val sensitive: Boolean = false,
		val fields: List<Field> = emptyList(),
		@SerialName("attribution_domains")
		val attributionDomains: List<String> = emptyList(),
		@SerialName("follow_requests_count")
		val followRequestsCount: Int = 0
	)
}