package site.remlit.snowdrop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Preferences(
	@SerialName("posting:default:visibility")
	val defaultVisibility: String? = null,
	@SerialName("posting:default:sensitive")
	val defaultSensitive: Boolean? = null,
	@SerialName("posting:default:language")
	val defaultLanguage: String? = null,
	@SerialName("posting:default:quote_policy")
	val defaultQuotePolicy: String? = null,
	@SerialName("reading:expand:media")
	val expandMedia: String? = null,
	@SerialName("reading:expand:spoilers")
	val expandSpoilers: Boolean? = null,
	@SerialName("reading:autoplay:gifs")
	val autoplayGifs: Boolean? = null
)
