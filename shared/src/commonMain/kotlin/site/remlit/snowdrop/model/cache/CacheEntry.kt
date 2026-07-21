package site.remlit.snowdrop.model.cache

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import site.remlit.snowdrop.util.config.json
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Entry in the app's cache.
 *
 * @param id ID of item
 * @param content JSON string of content
 * @param createdAt Instant of when this item was created for expiry purposes
 *
 * @since 0.0.2-alpha
 * */
@Serializable
data class CacheEntry(
	val id: String,
	val content: String,
	val createdAt: Instant = Clock.System.now()
) {
	/**
	 * Get content of a cache entry. Has chance of failure due to serialization
	 * error, should be wrapped in safe block.
	 *
	 * @return Cache entry content as T
	 * */
	@Throws(SerializationException::class)
	inline fun <reified T> getContent(): T {
		return json.decodeFromString<T>(content)
	}
}
