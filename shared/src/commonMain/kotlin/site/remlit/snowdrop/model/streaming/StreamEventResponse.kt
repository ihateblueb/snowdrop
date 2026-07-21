package site.remlit.snowdrop.model.streaming

import kotlinx.serialization.Serializable
import site.remlit.snowdrop.util.config.json

@Serializable
data class StreamEventResponse(
	val stream: List<String>,
	val event: String,
	val payload: String
) {
	inline fun <reified T> getPayload(): T? {
		// todo: safereturnable for these types of things?
		return json.decodeFromString<T>(payload)
	}
}
