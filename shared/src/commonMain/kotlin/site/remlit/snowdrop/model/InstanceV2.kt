package site.remlit.snowdrop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstanceV2(
	val domain: String,
	val title: String? = null,
	val description: String? = null,
	val version: String? = null,
	@SerialName("source_url")
	val sourceUrl: String? = null,

	val usage: Usage = Usage(),
	val thumbnail: Thumbnail = Thumbnail(),
	val icon: List<Icon> = emptyList(),
	val languages: List<String> = emptyList(),
	val configuration: Configuration = Configuration(),

	val apiVersions: ApiVersions = ApiVersions()
) {
	@Serializable
	data class Usage(
		val users: Users = Users()
	) {
		@Serializable
		data class Users(
			val activeMonth: Int? = null
		)
	}

	/**
	 * @param versions Map of @1x to URL where 1 is scale of thumbnail
	 * */
	@Serializable
	data class Thumbnail(
		val url: String? = null,
		val blurhash: String? = null,
		val versions: Map<String, String> = emptyMap(),
		val description: String? = null
	)

	@Serializable
	data class Icon(
		val src: String,
		val size: String
	)

	@Serializable
	data class Configuration(
		val urls: Urls = Urls(),
		val vapid: Vapid = Vapid(),
		val accounts: Accounts = Accounts(),
		val statuses: Statuses = Statuses(),
		@SerialName("media_attachments")
		val mediaAttachments: MediaAttachments = MediaAttachments(),
		val polls: Polls = Polls(),
		val translation: Translation = Translation()
	) {
		@Serializable
		data class Urls(
			val streaming: String? = null,
			val status: String? = null,
			val about: String? = null,
			@SerialName("privacy_policy")
			val privacyPolicy: String? = null,
			@SerialName("terms_of_service")
			val termsOfService: String? = null,
		)

		@Serializable
		data class Vapid(
			@SerialName("public_key")
			val publicKey: String? = null
		)

		@Serializable
		data class Accounts(
			@SerialName("max_display_name_length")
			val maxDisplayNameLength: Int = 0,
			@SerialName("max_note_length")
			val maxNoteLength: Int = 0,
			@SerialName("max_avatar_description_length")
			val maxAvatarDescriptionLength: Int = 0,
			@SerialName("max_header_description_length")
			val maxHeaderDescriptionLength: Int = 0,
			@SerialName("max_featured_tags")
			val maxFeaturedTags: Int = 0,
			@SerialName("max_pinned_statuses")
			val maxPinnedStatuses: Int = 0,
			@SerialName("max_profile_fields")
			val maxProfileFields: Int = 0,
			@SerialName("profile_field_name_limit")
			val profileFieldNameLimit: Int = 0,
			@SerialName("profile_field_value_limit")
			val profileFieldValueLimit: Int = 0,
		)

		@Serializable
		data class Statuses(
			@SerialName("max_characters")
			val maxCharacters: Int = 0,
			@SerialName("max_media_attachments")
			val maxMediaAttachments: Int = 0,
			@SerialName("characters_reserved_per_url")
			val charactersReservedPerUrl: Int = 0
		)

		@Serializable
		data class MediaAttachments(
			@SerialName("description_limit")
			val descriptionLimit: Int = 0,
			@SerialName("image_matrix_size_limit")
			val imageMatrixLimit: Int = 0,
			@SerialName("image_size_limit")
			val imageSizeLimit: Int = 0,
			@SerialName("supported_mime_types")
			val supportedMimeTypes: List<String> = emptyList(),
			@SerialName("video_frame_rate_limit")
			val videoFrameRateLimit: Int = 0,
			@SerialName("video_matrix_limit")
			val videoMatrixLimit: Int = 0,
			@SerialName("video_size_limit")
			val videoSizeLimit: Int = 0
		)

		@Serializable
		data class Polls(
			val maxOptions: Int = 0,
			val maxCharactersPerOption: Int = 0,
			val minExpiration: Int = 0,
			val maxExperation: Int = 0
		)

		@Serializable
		data class Translation(
			val enabled: Boolean = false
		)

		@Serializable
		data class TimelinesAccess(
			@SerialName("live_feeds")
			val liveFeeds: Feed = Feed("disabled", "disabled"),
			@SerialName("hashtag_feeds")
			val hashtagFeeds: Feed = Feed("disabled", "disabled"),
			@SerialName("trending_link_feeds")
			val trendingLinkFeeds: Feed = Feed("disabled", "disabled")
		) {
			@Serializable
			data class Feed(
				val local: String,
				val remote: String
			)
		}
	}

	@Serializable
	data class ApiVersions(
		val mastodon: Int? = null,
		val glitch: Int? = null,
		val chuckya: Int? = null,
		@SerialName("net.iceshrimp.scheduled_boosts")
		val netIceshrimpScheduledBoosts: Int? = null,
		@SerialName("download.synth.keyword_lists")
		val downloadSynthKeywordLists: Int? = null
	)
}
