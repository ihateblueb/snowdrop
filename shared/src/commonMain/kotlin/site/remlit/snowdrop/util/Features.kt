package site.remlit.snowdrop.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import site.remlit.snowdrop.api.instance.getInstanceV1
import site.remlit.snowdrop.api.instance.getInstanceV2
import site.remlit.snowdrop.model.InstanceV1
import site.remlit.snowdrop.model.InstanceV2

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
 * If determineFeatures() is actively running.
 * */
var determiningFeatures by mutableStateOf(false)

/**
 * Object of mutable states which advertise the current supported
 * features of the logged in account.
 * */
suspend fun determineFeatures() {
	determiningFeatures = true
	var software = Software.Mastodon

	var v2: InstanceV2?
	val resV2 = getInstanceV2(auth = true)
	v2 = if (resV2.error || resV2.response == null) null else resV2.response

	var v1: InstanceV1? = null
	if (v2 == null) {
		val resV1 = getInstanceV1(auth = true)
		if (resV1.error || resV1.response == null) return
		v1 = resV1.response
	}


	val version = v2?.version ?: v1?.version ?: ""

	// order is important here, check forks of a software *after* the software, otherwise chuckya would be marked as glitch
	// for also supporting it's api version
	// todo: find a real glitch version string
	if (version.contains(".*\\+glitch".toRegex()) ||
		(v2?.apiVersions?.glitch != null && v2.apiVersions.glitch > 0)
	) software = Software.Glitch

	if (version.contains(".*\\+chuckya".toRegex()) ||
		(v2?.apiVersions?.chuckya != null && v2.apiVersions.chuckya > 0)
	) software = Software.Chuckya

	// todo: test pleroma softwareMode
	if ("""\(compatible; Pleroma .*\)""".toRegex().containsMatchIn(version))
		software = Software.Pleroma

	if ("""\(compatible; Akkoma .*\)""".toRegex().containsMatchIn(version))
		software = Software.Akkoma

	if ("""\(compatible; Sharkey .*; like Akkoma\)""".toRegex().containsMatchIn(version))
		software = Software.Sharkey

	if ("""\(compatible; Iceshrimp .*\)""".toRegex().containsMatchIn(version))
		software = Software.IceshrimpJS

	if ("""\(compatible; Iceshrimp\.NET/.*\)""".toRegex().containsMatchIn(version))
		software = Software.IceshrimpNET


	Logger.d { "Detected software $software from version string \"${version}\" and api_versions \"${v2?.apiVersions}\"" }

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
	if (enabled == null && !determiningFeatures) bgIO { determineFeatures() }

	return enabled ?: false
}
