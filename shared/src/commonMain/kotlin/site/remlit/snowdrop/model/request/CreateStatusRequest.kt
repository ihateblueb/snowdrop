package site.remlit.snowdrop.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import site.remlit.snowdrop.model.Validatable

@Serializable
data class CreateStatusRequest(
	@SerialName("spoiler_text")
	val spoilerText: String? = null,
	val status: String? = null,

	@SerialName("in_reply_to_id")
	val inReplyToId: String? = null,

	@SerialName("local_only")
	val localOnly: Boolean = false,
	val sensitive: Boolean = false,
	val preview: Boolean = false,

	val visibility: String,
	val language: String? = null,
	@SerialName("scheduled_at")
	val scheduledAt: String? = null,
	@SerialName("media_ids")
	val mediaIds: List<String> = emptyList(),

	@SerialName("quote_id")
	val quoteId: String? = null,
	@SerialName("quoted_status_id")
	val quotedStatusId: String? = null,

	val poll: Poll? = null
) : Validatable {
	@Serializable
	data class Poll(
		val options: List<String>,
		@SerialName("expires_in")
		val expiresIn: Long,
		val multiple: Boolean = false,
		@SerialName("hide_totals")
		val hideTotals: Boolean = false,
	)

	override fun validate() {
		if (status.isNullOrBlank() && mediaIds.isEmpty())
			throw IllegalArgumentException("Status cannot be blank unless media is attached")
	}
}

