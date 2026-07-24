package site.remlit.snowdrop.util.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import org.jetbrains.compose.resources.StringResource
import site.remlit.snowdrop.util.annotatedString.simpleAnnotatedString
import site.remlit.snowdrop.util.translation
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.now
import snowdrop.shared.generated.resources.soon
import snowdrop.shared.generated.resources.x_day
import snowdrop.shared.generated.resources.x_day_simple
import snowdrop.shared.generated.resources.x_days
import snowdrop.shared.generated.resources.x_hour
import snowdrop.shared.generated.resources.x_hour_simple
import snowdrop.shared.generated.resources.x_hours
import snowdrop.shared.generated.resources.x_minute
import snowdrop.shared.generated.resources.x_minute_simple
import snowdrop.shared.generated.resources.x_minutes
import snowdrop.shared.generated.resources.x_second
import snowdrop.shared.generated.resources.x_second_simple
import snowdrop.shared.generated.resources.x_seconds
import snowdrop.shared.generated.resources.x_week
import snowdrop.shared.generated.resources.x_week_simple
import snowdrop.shared.generated.resources.x_weeks
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@Composable
fun Instant.toRelativeString(
	inverse: Boolean = false,
	short: Boolean = false,
	nowAlternate: StringResource? = null
): AnnotatedString {
	val now = Clock.System.now()
	val duration = if (!inverse) now-this else this-now

	val seconds = duration.inWholeSeconds
	val minutes = duration.inWholeMinutes
	val hours = duration.inWholeHours
	val days = duration.inWholeDays
	val weeks = (duration.inWholeDays.toDouble() / 7).roundToInt()

	return if (duration < (-5).seconds) { translation(Res.string.soon) }
	else if (duration <= 5.seconds) { if (nowAlternate != null) translation(nowAlternate) else translation(Res.string.now) }
	else if (duration < 1.minutes) {
		if (short) translation(Res.string.x_second_simple, mapOf("time" to simpleAnnotatedString("$seconds")))
		else if (seconds == 1L) translation(Res.string.x_second, mapOf("time" to simpleAnnotatedString("$seconds")))
		else translation(Res.string.x_seconds, mapOf("time" to simpleAnnotatedString("$seconds")))
	} else if (duration < 1.hours) {
		if (short) translation(Res.string.x_minute_simple, mapOf("time" to simpleAnnotatedString("$minutes")))
		else if (minutes == 1L) translation(Res.string.x_minute, mapOf("time" to simpleAnnotatedString("$minutes")))
		else translation(Res.string.x_minutes, mapOf("time" to simpleAnnotatedString("$minutes")))
	} else if (duration < 1.days) {
		if (short) translation(Res.string.x_hour_simple, mapOf("time" to simpleAnnotatedString("$hours")))
		else if (hours == 1L) translation(Res.string.x_hour, mapOf("time" to simpleAnnotatedString("$hours")))
		else translation(Res.string.x_hours, mapOf("time" to simpleAnnotatedString("$hours")))
	} else if (duration >= 1.days) {
		if (short) translation(Res.string.x_day_simple, mapOf("time" to simpleAnnotatedString("$days")))
		else if (days == 1L) translation(Res.string.x_day, mapOf("time" to simpleAnnotatedString("$days")))
		else translation(Res.string.x_days, mapOf("time" to simpleAnnotatedString("$days")))
	} else if (duration >= 7.days) {
		if (short) translation(Res.string.x_week_simple, mapOf("time" to simpleAnnotatedString("$weeks")))
		else if (weeks == 1) translation(Res.string.x_week, mapOf("time" to simpleAnnotatedString("$weeks")))
		else translation(Res.string.x_weeks, mapOf("time" to simpleAnnotatedString("$weeks")))
	} else simpleAnnotatedString("?")
}
