package site.remlit.snowdrop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import site.remlit.snowdrop.util.safeReturnable
import kotlin.time.Instant

@Serializable
data class Status(
	val id: String? = null,
	val account: User? = null,

	@SerialName("spoiler_text")
	val spoilerText: String? = null,
	val text: String? = null,
	val content: String? = null,

	val url: String? = null,
	val uri: String? = null,

	@SerialName("in_reply_to_id")
	val inReplyToId: String? = null,
	@SerialName("in_reply_to_account_id")
	val inReplyToAccountId: String? = null,

	val reblog: Status? = null,
	val quote: Status? = null,
	@SerialName("quote_id")
	val quoteId: String? = null,
	@SerialName("quote_approval")
	val quoteApproval: QuoteApproval? = null,

	@SerialName("content_type")
	val contentType: String? = null,

	@SerialName("replies_count")
	val repliesCount: Long = 0,
	@SerialName("reblogs_count")
	val reblogsCount: Long = 0,
	@SerialName("favourites_count")
	val favouritesCount: Long = 0,
	@SerialName("reactions_count")
	val reactionsCount: Long = 0,

	val reblogged: Boolean = false,
	val favourited: Boolean = false,
	val bookmarked: Boolean = false,

	val muted: Boolean = false,
	val sensitive: Boolean = false,
	val pinned: Boolean = false,

	val visibility: String? = null,

	val poll: Poll? = null,
	val filtered: List<Filtered>? = null,
	val mentions: List<Mention> = listOf(),
	@SerialName("media_attachments")
	val mediaAttachments: List<MediaAttachment> = listOf(),
	val emojis: List<Emoji> = listOf(),
	val reactions: List<Reaction> = listOf(),
	val tags: List<Tag> = listOf(),

	val card: Unit? = null,
	val application: Unit? = null,
	val language: String? = null,

	val state: String? = null,
	@SerialName("quoted_status")
	val quotedStatus: Status? = null,

	@SerialName("created_at")
	val createdAt: String? = null,
	@SerialName("edited_at")
	val editedAt: String? = null,
) {
	@Serializable
	data class QuoteApproval(
		val automatic: List<String> = listOf(),
		val manual: List<String> = listOf(),
		@SerialName("current_user")
		val currentUser: String? = null,
	)

	@Serializable
	data class Poll(
		val id: String,

		val expired: Boolean,
		val multiple: Boolean,
		val voted: Boolean,

		@SerialName("votes_count")
		val votesCount: Long = 0,
		@SerialName("voters_count")
		val votersCount: Long? = 0,

		@SerialName("own_votes")
		val ownVotes: List<Int>,

		val options: List<Option>,
		val emojis: List<Emoji>,

		@SerialName("expires_at")
		val expiresAt: String? = null
	) {
		@Serializable
		data class Option(
			val title: String,
			@SerialName("votes_count")
			val votesCount: Long,
		)
	}

	@Serializable
	data class Filtered(
		val filter: Filter,
		@SerialName("keyword_matches")
		val keywordMatches: List<String> = listOf(),
		@SerialName("status_matches")
		val statusMatches: List<String> = listOf()
	) {
		@Serializable
		data class Filter(
			val id: String,
			val title: String? = null,
			val context: List<String> = listOf(),

			@SerialName("filter_action")
			val filterAction: String,
			val keywords: List<Keyword>,
			val statuses: List<String> = listOf(),

			@SerialName("expires_at")
			val expiresAt: String? = null
		) {
			@Serializable
			data class Keyword(
				val id: String,
				val keyword: String,
				@SerialName("whole_word")
				val wholeWord: Boolean
			)
		}
	}

	@Serializable
	data class Mention(
		val id: String,
		val username: String,
		val acct: String,
		val url: String
	)

	@Serializable
	data class MediaAttachment(
		val id: String,

		val url: String,
		@SerialName("remote_url")
		val remoteUrl: String?,
		@SerialName("preview_url")
		val previewUrl: String,
		@SerialName("text_url")
		val textUrl: String?,

		val meta: Meta? = null,
		val description: String? = null,
		val blurhash: String? = null,
		val type: String
	) {
		@Serializable
		data class Meta(
			val original: Original? = null
		) {
			@Serializable
			data class Original(
				val width: Long = 0,
				val height: Long = 0,
				val size: String? = null,
				val aspect: Double = 0.0,
			)
		}
	}

	@Serializable
	data class Reaction(
		val count: Long = 0,
		val me: Boolean = false,
		val name: String,
		val url: String? = null,
		@SerialName("static_url")
		val staticUrl: String? = null,
		val accounts: List<User>? = emptyList(),
		@SerialName("account_ids")
		val accountIds: List<String> = emptyList()
	)

	@Serializable
	data class Tag(
		val name: String,
		val url: String
	)

	// Instants
	fun getCreatedAtTimestamp(): Instant? = safeReturnable {
		Instant.parse(this.createdAt!!)
	}
}
