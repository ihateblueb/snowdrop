package site.remlit.snowdrop.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import site.remlit.snowdrop.api.getInstance

enum class Software {
	Mastodon,
	Glitch,
	Chuckya,
	Pleroma,
	Akkoma,
	Sharkey,
	IceshrimpJS,
	IceshrimpNET
}

/**
 * If determineFeatures method is actively running.
 *
 * @since 0.0.1-alpha
 * */
var determiningFeatures by mutableStateOf(false)

/**
 * Check what the currently logged in API supports and note it so
 * the app can change its behavior accordingly.
 *
 * @since 0.0.1-alpha
 * */
suspend fun determineFeatures() {
	determiningFeatures = true
	var software = Software.Mastodon

	val res = getInstance(auth = true)
	if (res.error || res.response == null) return
	val instance = res.response


	if (instance.version.contains(".*\\+chuckya".toRegex()))
		software = Software.Chuckya


	// todo: detect glitch

	// todo: test pleroma softwareMode
	if ("""\(compatible; Pleroma .*\)""".toRegex().containsMatchIn(instance.version))
		software = Software.Pleroma

	if ("""\(compatible; Akkoma .*\)""".toRegex().containsMatchIn(instance.version))
		software = Software.Akkoma

	if ("""\(compatible; Sharkey .*; like Akkoma\)""".toRegex().containsMatchIn(instance.version))
		software = Software.Sharkey

	if ("""\(compatible; Iceshrimp .*\)""".toRegex().containsMatchIn(instance.version))
		software = Software.IceshrimpJS

	if ("""\(compatible; Iceshrimp\.NET/.*\)""".toRegex().containsMatchIn(instance.version))
		software = Software.IceshrimpNET


	Logger.d { "Detected software $software from version string \"${instance.version}\"" }

	if (
		software == Software.Chuckya ||
		software == Software.Akkoma ||
		software == Software.Sharkey ||
		software == Software.IceshrimpJS ||
		software == Software.IceshrimpNET
	) putFeature("reactions", true)
	else putFeature("reactions", false)

	// sharkey has a bubble tl but its unlikely it has good mastoapi support
	if (
		software == Software.Chuckya ||
		software == Software.IceshrimpJS ||
		software == Software.IceshrimpNET
	) putFeature("bubble_timeline", true)
	else putFeature("bubble_timeline", false)

	// and akkoma has a different route!
	if (
		software == Software.Akkoma
	) putFeature("bubble_timeline_akkoma", true)
	else putFeature("bubble_timeline_akkoma", false)

	if (
		software == Software.IceshrimpNET
	) putFeature("biting", true)
	else putFeature("biting", false)

	determiningFeatures = false
}

/**
 * Removes feature determinations so it will be run again.
 *
 * @since 0.0.1-alpha
 * */
fun resetFeatures() = blockingSettings.keys.filter {
	it.startsWith("feature_${getCurrentAccountId()}")
}.forEach {
	blockingSettings.remove(it)
}

/**
 * Sets a feature determination
 *
 * @since 0.0.1-alpha
 * */
fun putFeature(feature: String, value: Boolean) = blockingSettings.putBoolean(
	"feature_${getCurrentAccountId()}_$feature", value
)

/**
 * Gets a feature determination
 *
 * @since 0.0.1-alpha
 * */
fun getFeature(feature: String): Boolean {
	val enabled = blockingSettings.getBooleanOrNull(
		"feature_${getCurrentAccountId()}_$feature"
	)

	// feature set may have updated, so check again!
	if (enabled == null && !determiningFeatures) bgIO { determineFeatures() }

	return enabled ?: false
}
