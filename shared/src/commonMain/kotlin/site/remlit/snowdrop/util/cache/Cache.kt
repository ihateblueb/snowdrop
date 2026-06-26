package site.remlit.snowdrop.util.cache

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toBlockingSettings
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToHexString
import site.remlit.snowdrop.model.cache.CacheEntry
import site.remlit.snowdrop.model.cache.CacheManifest
import site.remlit.snowdrop.util.config.cbor
import site.remlit.snowdrop.util.config.json
import site.remlit.snowdrop.util.getCurrentAccountId

/* I don't think this will be used, but i'm keeping it just in case */
@OptIn(ExperimentalSettingsApi::class)
expect val cache: FlowSettings

@OptIn(ExperimentalSettingsApi::class)
val blockingCache = cache.toBlockingSettings()


@OptIn(ExperimentalSerializationApi::class)
fun setupCache() {
	if (blockingCache.hasKey("${getCurrentAccountId()}_manifest")) return
	blockingCache.putString(
		"${getCurrentAccountId()}_manifest",
		cbor.encodeToHexString(CacheManifest())
	)
}

@OptIn(ExperimentalSerializationApi::class)
fun getCacheManifest(): CacheManifest {
	val raw = blockingCache.getStringOrNull("${getCurrentAccountId()}_manifest")
		?: return CacheManifest()
	return cbor.decodeFromHexString(raw)
}

@OptIn(ExperimentalSerializationApi::class)
fun getCacheEntry(id: String): CacheEntry? {
	val raw = blockingCache.getStringOrNull("${getCurrentAccountId()}_entry_$id")
		?: return null
	return cbor.decodeFromHexString(raw)
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> putCacheEntry(
	id: String,
	content: T
) {
	val entry = CacheEntry(
		id,
		json.encodeToString<T>(content)
	)

	val manifest = getCacheManifest()
	blockingCache.putString(
		"${getCurrentAccountId()}_manifest",
		cbor.encodeToHexString(
			manifest.copy(ids = manifest.ids.plus(id).distinct())
		)
	)

	blockingCache.putString(
		"${getCurrentAccountId()}_entry_$id",
		cbor.encodeToHexString(entry)
	)
}

fun cleanExpiredCacheEntries() {

}
