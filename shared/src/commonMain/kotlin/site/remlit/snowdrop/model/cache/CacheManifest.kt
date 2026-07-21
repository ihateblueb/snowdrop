package site.remlit.snowdrop.model.cache

import kotlinx.serialization.Serializable

/**
 * Holds a list of IDs of every cache entry.
 * @since 0.0.2-alpha
 * */
@Serializable
data class CacheManifest(
	val ids: List<String> = emptyList()
)
