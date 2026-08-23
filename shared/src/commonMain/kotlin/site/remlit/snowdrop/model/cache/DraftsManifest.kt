package site.remlit.snowdrop.model.cache

import kotlinx.serialization.Serializable

@Serializable
data class DraftsManifest(
	/** First is ID, second is timestamp of creation */
	val ids: Map<String, String> = emptyMap()
)
