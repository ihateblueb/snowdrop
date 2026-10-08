package site.remlit.snowdrop.util.extension

import site.remlit.snowdrop.util.safeReturnable
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.time.Instant

actual fun Instant.toLocalizedString(): String {
	val parser = SimpleDateFormat(instantFormat, Locale.getDefault())
		.apply { timeZone = TimeZone.getTimeZone("GMT") }
	val formatter = DateFormat.getDateTimeInstance()

	return safeReturnable {
		val date = parser.parse(this.toString()) ?: return this.toString()
		formatter.format(date)
	} ?: this.toString()
}
