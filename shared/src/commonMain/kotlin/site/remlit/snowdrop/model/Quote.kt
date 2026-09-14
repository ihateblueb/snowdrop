package site.remlit.snowdrop.model

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
@Serializable(with = QuoteSerializer::class)
interface Quote

@Serializable
data class MastodonQuote(
	val state: String? = null,
	@SerialName("quoted_status")
	val quotedStatus: Status? = null
): Quote

object QuoteSerializer : JsonContentPolymorphicSerializer<Quote>(Quote::class) {
	override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Quote> = when {
		"quoted_status" in element.jsonObject -> MastodonQuote.serializer()
		else -> Status.serializer() // akkoma-style is just a status
	}
}
