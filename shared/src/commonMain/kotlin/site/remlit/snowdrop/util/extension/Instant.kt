package site.remlit.snowdrop.util.extension

import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

fun Instant.toRelativeString(): String {
	val now = Clock.System.now()
	val duration = now - this

	return if (duration < 5.seconds) "now"
	else if (duration < 1.minutes) "${duration.inWholeSeconds}s"
	else if (duration < 1.hours) "${duration.inWholeMinutes}m"
	else if (duration < 1.days) "${duration.inWholeHours}h"
	else if (duration >= 1.days) "${duration.inWholeDays}d"
	else if (duration >= 7.days) "${(duration.inWholeDays.toDouble()/7).roundToInt()}w"
	else "?"
}
