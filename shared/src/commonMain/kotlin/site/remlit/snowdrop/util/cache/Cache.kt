package site.remlit.snowdrop.util.cache

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toBlockingSettings
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToHexString
import site.remlit.snowdrop.model.cache.CacheEntry
import site.remlit.snowdrop.model.cache.CacheManifest
import site.remlit.snowdrop.util.config.cbor
import site.remlit.snowdrop.util.config.json
import site.remlit.snowdrop.util.getCurrentAccountId

@OptIn(ExperimentalSettingsApi::class)
expect val cache: FlowSettings

@OptIn(ExperimentalSettingsApi::class)
val blockingCache = cache.toBlockingSettings()

val cacheCoroutineScope = CoroutineScope(Dispatchers.Default + CoroutineName("Cache"))

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
fun setupCache() {
	cacheCoroutineScope.launch {
		if (cache.hasKey("${getCurrentAccountId()}_manifest")) return@launch
		cache.putString(
			"${getCurrentAccountId()}_manifest",
			cbor.encodeToHexString(CacheManifest())
		)
	}
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
fun getCacheEntry(accountId: String, id: String): CacheEntry? {
	val raw = blockingCache.getStringOrNull("${accountId}_entry_$id")
		?: return null
	return cbor.decodeFromHexString(raw)
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
inline fun <reified T> putCacheEntry(
	id: String,
	content: T
) {
	cacheCoroutineScope.launch {
		val entry = CacheEntry(
			id,
			json.encodeToString<T>(content)
		)

		val manifest = getCacheManifest()
		cache.putString(
			"${getCurrentAccountId()}_manifest",
			cbor.encodeToHexString(
				manifest.copy(ids = manifest.ids.plus(id).distinct())
			)
		)

		cache.putString(
			"${getCurrentAccountId()}_entry_$id",
			cbor.encodeToHexString(entry)
		)
	}
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
fun removeCacheEntry(id: String) {
	cacheCoroutineScope.launch {
		val manifest = getCacheManifest()
		cache.putString(
			"${getCurrentAccountId()}_manifest",
			cbor.encodeToHexString(
				manifest.copy(ids = manifest.ids.minus(id).distinct())
			)
		)

		cache.remove("${getCurrentAccountId()}_entry_$id")
	}
}

fun clearCacheEntries() = getCacheManifest().ids.forEach { removeCacheEntry(it) }
