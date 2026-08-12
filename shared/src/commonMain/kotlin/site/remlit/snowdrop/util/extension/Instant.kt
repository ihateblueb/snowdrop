package site.remlit.snowdrop.util.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import org.jetbrains.compose.resources.StringResource
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
	else if (duration < 1.minutes) translation(
		res = if (short) Res.plurals.x_second_simple else Res.plurals.x_second,
		quantity = seconds.toInt(),
		replacements = mapOf("time" to AnnotatedString("$seconds"))
	) else if (duration < 1.hours) translation(
		res = if (short) Res.plurals.x_minute_simple else Res.plurals.x_minute,
		quantity = minutes.toInt(),
		replacements = mapOf("time" to AnnotatedString("$minutes"))
	) else if (duration < 1.days) translation(
		res = if (short) Res.plurals.x_hour_simple else Res.plurals.x_hour,
		quantity = hours.toInt(),
		replacements = mapOf("time" to AnnotatedString("$hours"))
	) else if (duration >= 1.days) translation(
		res = if (short) Res.plurals.x_day_simple else Res.plurals.x_day,
		quantity = days.toInt(),
		replacements = mapOf("time" to AnnotatedString("$days"))
	) else if (duration >= 7.days) translation(
		res = if (short) Res.plurals.x_week_simple else Res.plurals.x_week,
		quantity = weeks,
		replacements = mapOf("time" to AnnotatedString("$weeks"))
	) else AnnotatedString("?")
}

/**
 * Format to provide to native date formatters for parsing Kotlin Instants
 * @since 0.0.6-alpha
 * */
const val instantFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'"

/**
 * Get a localized string from an instant.
 *
 * @return Localized string (e.g. 27 July 2026 at 12:35pm)
 * @since 0.0.6-alpha
 * */
expect fun Instant.toLocalizedString(): String
