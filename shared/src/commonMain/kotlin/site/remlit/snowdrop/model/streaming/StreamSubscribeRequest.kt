package site.remlit.snowdrop.model.streaming

import kotlinx.serialization.Serializable

@Serializable
data class StreamSubscribeRequest(
	val type: String = "subscribe",
	val stream: String
)
