package site.remlit.snowdrop.util

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
 * Object of mutable states which advertise the current supported
 * features of the logged in account.
 * */
suspend fun determineFeatures() {
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
}

fun resetFeatures() = blockingSettings.keys.filter {
	it.startsWith("feature_${getCurrentAccountId()}")
}.forEach {
	blockingSettings.remove(it)
}

fun putFeature(feature: String, value: Boolean) = blockingSettings.putBoolean(
	"feature_${getCurrentAccountId()}_$feature", value
)

fun getFeature(feature: String): Boolean {
	val enabled = blockingSettings.getBooleanOrNull(
		"feature_${getCurrentAccountId()}_$feature"
	)

	// feature set may have updated, so check again!
	if (enabled == null) bgIO { determineFeatures() }

	return enabled ?: false
}
