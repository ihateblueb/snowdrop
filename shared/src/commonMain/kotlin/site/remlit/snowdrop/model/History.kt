package site.remlit.snowdrop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class History(
	// doesn't have an id, so we make one up lol
	override val id: String = Uuid.random().toString(),

	val content: String? = null,
	@SerialName("spoiler_text")
	val spoilerText: String? = null,
	val sensitive: Boolean = false,
	@SerialName("created_at")
	val createdAt: String,
	val account: Account,
	@SerialName("media_attachments")
	val mediaAttachments: List<Status.MediaAttachment> = emptyList(),
	val emojis: List<Emoji> = emptyList()
) : IdentifiableObject<String>
