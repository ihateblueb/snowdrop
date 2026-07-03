package site.remlit.snowdrop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstanceV1(
	val stats: Stats,
	val version: String,
	@SerialName("max_toot_chars")
	val maxTootChars: Int,
	val uri: String? = null,
	val title: String,
	val email: String? = null,

	@SerialName("short_description")
	val shortDescription: String? = null,
	val description: String? = null,

	val registrations: Boolean = false,
	@SerialName("invites_enabled")
	val invitesEnabled: Boolean = false,
	@SerialName("approval_required")
	val approvalRequired: Boolean = false,

	val urls: Urls? = null,
	val configuration: Configuration? = null,

	val rules: List<Rule> = emptyList(),
	val thumbnail: String? = null
) {
	@Serializable
	data class Stats(
		@SerialName("user_count")
		val userCount: Long,
		@SerialName("status_count")
		val statusCount: Long,
		@SerialName("domain_count")
		val domainCount: Long
	)

	@Serializable
	data class Urls(
		val streaming: String? = null
	)

	@Serializable
	data class Configuration(
		val accounts: Accounts? = null,
		val statuses: Statuses? = null,
		@SerialName("media_attachments")
		val mediaAttachments: MediaAttachments? = null,
		val reactions: Reactions? = null,
	) {
		@Serializable
		data class Accounts(
			@SerialName("max_featured_tags")
			val maxFeaturedTags: Int = 0,
			@SerialName("max_note_length")
			val maxNoteLength: Int = 0,
			@SerialName("max_display_name_length")
			val maxDisplayNameLength: Int = 0,
			@SerialName("max_profile_fields")
			val maxProfileFields: Int = 0,
			@SerialName("profile_field_name_limit")
			val profileFieldNameLimit: Int = 0,
			@SerialName("profile_field_value_limit")
			val profileFieldValueLimit: Int = 0,
		)

		@Serializable
		data class Statuses(
			@SerialName("supported_mine_types")
			val supportedMimeTypes: List<String> = emptyList(),
			@SerialName("max_characters")
			val maxCharacters: Int = 0,
			@SerialName("max_media_attachments")
			val maxMediaAttachments: Int = 0,
			@SerialName("characters_reserved_per_url")
			val charactersReservedPerUrl: Int = 0
		)

		@Serializable
		data class MediaAttachments(
			@SerialName("supported_mine_types")
			val supportedMimeTypes: List<String> = emptyList(),
			@SerialName("image_size_limit")
			val imageSizeLimit: Int = 0,
			@SerialName("image_matrix_limit")
			val imageMatrixLimit: Int = 0,
			@SerialName("video_size_limit")
			val videoSizeLimit: Int = 0,
			@SerialName("video_frame_limit")
			val videoFrameLimit: Int = 0,
			@SerialName("video_matrix_limit")
			val videoMatrixLimit: Int = 0,
		)

		@Serializable
		data class Polls(
			@SerialName("allow_media")
			val allowMedia: Boolean = false,
			@SerialName("max_options")
			val maxOptions: Int = 0,
			@SerialName("max_characters_per_option")
			val maxCharactersPerOption: Int = 0,
			@SerialName("min_expiration")
			val minExpiration: Int = 0,
			@SerialName("max_expiration")
			val maxExpiration: Int = 0,
		)

		@Serializable
		data class Reactions(
			@SerialName("Max_reactions")
			val maxReactions: Int = 0
		)
	}

	@Serializable
	data class Rule(
		val id: String,
		val text: String,
		val hint: String
	)
}
