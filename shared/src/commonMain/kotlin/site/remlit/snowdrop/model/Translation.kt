package site.remlit.snowdrop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Translation(
	val content: String,
	@SerialName("spoiler_text")
	val spoilerText: String,
	val language: String,
	val poll: Poll? = null,
	@SerialName("media_attachments")
	val mediaAttachments: List<Attachment>? = listOf(),
	@SerialName("detected_source_language")
	val detectedSourceLanguage: String,
	val provider: String
) {
	@Serializable
	data class Poll(
		val id: String,
		val options: List<Option>
	) {
		@Serializable
		data class Option(
			val title: String
		)
	}

	@Serializable
	data class Attachment(
		val id: String,
		val description: String
	)
}
