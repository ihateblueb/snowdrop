package site.remlit.snowdrop.util.extension

import android.icu.text.SimpleDateFormat
import site.remlit.snowdrop.util.safeReturnable
import java.util.Locale
import java.util.TimeZone
import kotlin.time.Instant

actual fun Instant.toLocalizedString(): String {
	val parser = java.text.SimpleDateFormat(instantFormat, Locale.getDefault())
		.apply { this.timeZone = TimeZone.getTimeZone("GMT") }
	val formatter = SimpleDateFormat.getDateTimeInstance()

	return safeReturnable {
		val date = parser.parse(this.toString()) ?: return this.toString()
		formatter.format(date)
	} ?: this.toString()
}
